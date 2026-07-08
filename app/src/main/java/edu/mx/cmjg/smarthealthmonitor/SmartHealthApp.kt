package edu.mx.cmjg.smarthealthmonitor

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import edu.mx.cmjg.smarthealthmonitor.data.SmartHealthRepository

class SmartHealthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)
        
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
