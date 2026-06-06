package edu.mx.cmjg.smarthealthmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import edu.mx.cmjg.smarthealthmonitor.data.SmartHealthRepository
import edu.mx.cmjg.smarthealthmonitor.data.models.MockData

class DashboardViewModel : ViewModel() {

    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .map { if (it == 0) MockData.fcActual else it }
        .stateIn(
            scope          = viewModelScope,
            started        = SharingStarted.WhileSubscribed(5_000),
            initialValue   = MockData.fcActual
        )

    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .map { if (it == 0) MockData.pasosActual else it }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = MockData.pasosActual
        )

    val historial = SmartHealthRepository.obtenerHistorial().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // 🌟 AGREGA ESTA FUNCIÓN PARA LA SIMULACIÓN
    fun simularDatosWearable() {
        val fcAleatoria = (60..110).random()
        val pasosAleatorios = (3000..8000).random()

        // Si tu SmartHealthRepository tiene funciones para cambiar el valor, descoméntalas aquí:
        viewModelScope.launch {
            SmartHealthRepository.actualizarFC(fcAleatoria)
        }
        SmartHealthRepository.actualizarPasos(pasosAleatorios)
    }
}