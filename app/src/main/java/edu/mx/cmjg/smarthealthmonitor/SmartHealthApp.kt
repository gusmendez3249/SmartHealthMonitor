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
        
        CoroutineScope(Dispatchers.IO).launch {
            SmartHealthRepository.limpiarHistorialAntiguo()
        }
    }
}
