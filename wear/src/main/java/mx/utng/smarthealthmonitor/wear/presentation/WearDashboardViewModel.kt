package mx.utng.smarthealthmonitor.wear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mx.utng.smarthealthmonitor.wear.data.SmartHealthRepository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.wear.mqtt.MqttWearPublisher

class WearDashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val mqttPublisher = MqttWearPublisher(application)
    private val neonRepo = mx.utng.smarthealthmonitor.wear.data.WearNeonRepository()
    
    init {
        mqttPublisher.connect()
        viewModelScope.launch {
            SmartHealthRepository.fcFlow.collect { rawBpm ->
                val bpm = if (rawBpm == 0) 72 else rawBpm // default if 0
                val estado = when { bpm < 60 -> "FC Baja"; bpm > 100 -> "FC Alta"; else -> "Normal" }
                android.util.Log.d("MQTT_WEAR", "Detectado latido en ViewModel: $bpm. Intentando publicar...")
                mqttPublisher.publishFC(bpm, estado)
                
                // Publicar a Neon en IO thread
                launch(kotlinx.coroutines.Dispatchers.IO) {
                    kotlin.runCatching { neonRepo.publicarLectura(bpm, estado) }
                        .onFailure { android.util.Log.w("WEAR","Sin red: ${it.message}") }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mqttPublisher.disconnect()
    }
 
    // Usamos el Repository local del módulo wear
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .map { if (it == 0) 72 else it }  // valor por defecto
        .stateIn(viewModelScope,
                 SharingStarted.WhileSubscribed(5_000), 72)
                 
    // ← NUEVO: historial
    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(viewModelScope,
                     SharingStarted.WhileSubscribed(5_000),
                     emptyList())
}
