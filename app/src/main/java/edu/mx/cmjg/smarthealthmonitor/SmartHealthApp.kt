package edu.mx.cmjg.smarthealthmonitor

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import edu.mx.cmjg.smarthealthmonitor.data.SmartHealthRepository
import edu.mx.cmjg.smarthealthmonitor.mqtt.MqttAppService

class SmartHealthApp : Application() {
    lateinit var mqttService: MqttAppService
    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)
        
        mqttService = MqttAppService(context = this)
        mqttService.connect()
        
        // Inicializar Cast SDK al arrancar la app
        try {
            com.google.android.gms.cast.framework.CastContext.getSharedInstance(this)  // lazy init
        } catch (e: Exception) {
            // Cast no disponible en este dispositivo (emulador sin GMS)
            android.util.Log.w("Cast", "Cast not available: ${e.message}")
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            SmartHealthRepository.limpiarHistorialAntiguo()
        }
    }
}
