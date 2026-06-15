package mx.utng.smarthealthmonitor.wear.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SmartHealthRepository {
    private val _fcFlow = MutableStateFlow(0)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    fun actualizarFC(bpm: Int) {
        _fcFlow.value = bpm
    }

    fun obtenerHistorial() = kotlinx.coroutines.flow.flowOf(
        listOf(
            mx.utng.smarthealthmonitor.wear.presentation.LecturaFC(1, 72, true, "08:00"),
            mx.utng.smarthealthmonitor.wear.presentation.LecturaFC(2, 120, false, "09:30"),
            mx.utng.smarthealthmonitor.wear.presentation.LecturaFC(3, 75, true, "10:00")
        )
    )

    fun startMeasureClient(context: android.content.Context) {
        val measureClient = androidx.health.services.client.HealthServices.getClient(context).measureClient
        measureClient.registerMeasureCallback(
            androidx.health.services.client.data.DataType.HEART_RATE_BPM,
            object : androidx.health.services.client.MeasureCallback {
                override fun onAvailabilityChanged(
                    dataType: androidx.health.services.client.data.DeltaDataType<*, *>,
                    availability: androidx.health.services.client.data.Availability
                ) {}

                override fun onDataReceived(data: androidx.health.services.client.data.DataPointContainer) {
                    val dataPoints = data.getData(androidx.health.services.client.data.DataType.HEART_RATE_BPM)
                    dataPoints.forEach { point ->
                        if (point is androidx.health.services.client.data.SampleDataPoint<Double>) {
                            actualizarFC(point.value.toInt())
                        }
                    }
                }
            }
        )
    }
}
