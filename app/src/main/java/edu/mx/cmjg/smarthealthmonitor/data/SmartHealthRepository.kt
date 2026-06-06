package edu.mx.cmjg.smarthealthmonitor.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import android.content.Context
import edu.mx.cmjg.smarthealthmonitor.data.db.LecturaFCDao
import edu.mx.cmjg.smarthealthmonitor.data.db.SmartHealthDB
import edu.mx.cmjg.smarthealthmonitor.data.models.LecturaFC

/**
 * Repositorio singleton que centraliza los datos de salud.
 * El WearListenerService escribe aquí.
 * El ViewModel lee de aquí.
 */
object SmartHealthRepository {

    // FC actual del wearable (bpm)
    private val _fcFlow = MutableStateFlow(0)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    // Pasos del día actual
    private val _pasosFlow = MutableStateFlow(0)
    val pasosFlow: StateFlow<Int> = _pasosFlow.asStateFlow()

    private var dao: LecturaFCDao? = null

    fun init(context: Context) {
        dao = SmartHealthDB.getDatabase(context).lecturaDao()
    }

    suspend fun actualizarFC(bpm: Int) {
        _fcFlow.value = bpm
        withContext(Dispatchers.IO) {
            dao?.insertar(LecturaFC(valorBpm = bpm))
        }
    }

    fun actualizarPasos(pasos: Int) {
        _pasosFlow.value = pasos
    }

    fun obtenerHistorial(): Flow<List<LecturaFC>> =
        dao?.obtenerUltimas() ?: emptyFlow()

    suspend fun limpiarHistorialAntiguo() {
        val limite = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
        withContext(Dispatchers.IO) {
            dao?.limpiarViejos(limite)
        }
    }
}


