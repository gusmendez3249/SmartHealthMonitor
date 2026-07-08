package mx.utng.smarthealthmonitor.data

import mx.utng.smarthealthmonitor.tv.LecturaFC

object MockData {
    val historialFC = listOf(
        LecturaFC(1, 75, "10:00 AM"),
        LecturaFC(2, 68, "12:00 PM")
    )
    
    val alertasRecientes = listOf(
        LecturaFC(10, 140, "11:00 AM"),
        LecturaFC(11, 45, "02:00 PM"),
        LecturaFC(12, 125, "05:00 PM")
    )
}
