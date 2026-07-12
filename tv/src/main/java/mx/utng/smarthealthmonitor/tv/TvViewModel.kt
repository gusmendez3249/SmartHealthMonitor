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

    // Flow de mensajes MQTT entrantes
    private val mqttFlow = MutableStateFlow<TvMessage?>(null)
    private val mqttSubscriber = MqttTvSubscriber(application, mqttFlow)
 
    init {
        mqttSubscriber.connect()
 
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
                        if (nuevaLista.size > 10) nuevaLista.removeAt(0)
                        nuevaLista.add(LecturaFC(id = System.currentTimeMillis().toInt(), valorBpm = tvMsg.bpm, hora = tvMsg.hora))
                        _historial.value = nuevaLista
                    }
                }
            }
        }
    }

    override fun onCleared() {
        mqttSubscriber.disconnect()
        super.onCleared()
    }
}
