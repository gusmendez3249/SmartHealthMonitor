package mx.utng.smarthealthmonitor.tv
 
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.data.MockData
import mx.utng.smarthealthmonitor.tv.mqtt.MqttTvSubscriber
import mx.utng.smarthealthmonitor.tv.mqtt.TvMessage
 
class TvViewModel(application: Application) : AndroidViewModel(application) {
 
    private val _fc = MutableStateFlow(88)
    val fc: StateFlow<Int> = _fc.asStateFlow()
 
    private val _historial = MutableStateFlow(MockData.historialFC)
    val historial: StateFlow<List<LecturaFC>> = _historial.asStateFlow()
    
    private val _estadisticas = MutableStateFlow<List<LecturaFC>>(emptyList())
    val estadisticas: StateFlow<List<LecturaFC>> = _estadisticas.asStateFlow()
 
    private val _avanzadas = MutableStateFlow<List<LecturaFC>>(emptyList())
    val avanzadas: StateFlow<List<LecturaFC>> = _avanzadas.asStateFlow()

    // Flow de mensajes MQTT entrantes
    private val mqttFlow = MutableStateFlow<TvMessage?>(null)
    private val mqttSubscriber = MqttTvSubscriber(application, mqttFlow)
    
    private val neonRepo = mx.utng.smarthealthmonitor.tv.data.TvNeonRepository()
 
    init {
        mqttSubscriber.connect()
        cargarDatos()
 
        // Observar mensajes MQTT y actualizar el estado de la UI
        viewModelScope.launch {
            mqttFlow.collect { tvMsg ->
                if (tvMsg != null) {
                    _fc.value = tvMsg.bpm
                    
                    // Agregar al historial simulado solo si el BPM es diferente al último
                    val nuevaLista = _historial.value.toMutableList()
                    val ultimoBpm = nuevaLista.lastOrNull()?.valorBpm
                    if (ultimoBpm != tvMsg.bpm) {
                        // Mantener un historial corto
                        if (nuevaLista.size > 50) nuevaLista.removeAt(0)
                        nuevaLista.add(0, LecturaFC(id = System.currentTimeMillis().toInt(), valorBpm = tvMsg.bpm, hora = tvMsg.hora))
                        _historial.value = nuevaLista
                    }
                }
            }
        }
    }
    
    fun cargarDatos() {
        viewModelScope.launch {
            try {
                val lecturas = neonRepo.obtenerHistorialCompleto(50)
                _historial.value = lecturas.map { dto ->
                    LecturaFC(
                        id = dto.id,
                        valorBpm = dto.bpm,
                        hora = dto.hora
                    )
                }
                
                val stats = neonRepo.obtenerEstadisticas()
                _estadisticas.value = stats.map { dto ->
                    LecturaFC(
                        id = dto.id,
                        valorBpm = dto.bpm,
                        hora = "${dto.dispositivo}: ${dto.bpm} avg"
                    )
                }
                
                val avanzadasList = mutableListOf<LecturaFC>()
                
                // 1. Alertas
                neonRepo.obtenerAlertas().forEach {
                    avanzadasList.add(LecturaFC(id = it.id, valorBpm = it.bpm, hora = "Alerta ${it.dispositivo}: ${it.hora}"))
                }
                
                // 2. Promedio por Hora
                neonRepo.obtenerPromedioHora().forEach {
                    val horaDia = it.hora_dia?.toInt() ?: 0
                    val prom = it.promedio_bpm?.toInt() ?: 0
                    avanzadasList.add(LecturaFC(id = horaDia, valorBpm = prom, hora = "Avg a las $horaDia:00"))
                }
                
                // 3. Reciente por dispositivo
                neonRepo.obtenerLecturaMasRecientePorDispositivo().forEach {
                    avanzadasList.add(LecturaFC(id = it.id, valorBpm = it.bpm, hora = "Última de ${it.dispositivo}"))
                }
                
                // 4. Taquicardias
                neonRepo.obtenerTaquicardiaSostenida().forEach {
                    val count = it.lecturas_altas ?: 0
                    if(count > 0) {
                        avanzadasList.add(LecturaFC(id = 0, valorBpm = count, hora = "Taquicardias: $count (desde ${it.desde})"))
                    }
                }
                
                _avanzadas.value = avanzadasList
                
            } catch (e: Exception) {
                android.util.Log.e("TvViewModel", "Error fetching from Neon", e)
            }
        }
    }

    override fun onCleared() {
        mqttSubscriber.disconnect()
        super.onCleared()
    }
}
