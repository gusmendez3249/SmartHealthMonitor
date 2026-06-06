package edu.mx.cmjg.smarthealthmonitor.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecturas_fc")
data class LecturaFC(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val valorBpm: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val hora: String = java.text.SimpleDateFormat(
        "HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date()),
    val esNormal: Boolean = valorBpm in 60..100
)

// Datos de prueba para desarrollo (mock data)
object MockData {
    val historialFC = listOf(
        LecturaFC(id = 1, valorBpm = 78, hora = "11:00"),
        LecturaFC(id = 2, valorBpm = 82, hora = "10:30"),
        LecturaFC(id = 3, valorBpm = 76, hora = "10:00"),
        LecturaFC(id = 4, valorBpm = 95, hora = "09:30", esNormal = false),
        LecturaFC(id = 5, valorBpm = 71, hora = "09:00"),
        LecturaFC(id = 6, valorBpm = 80, hora = "08:30"),
        LecturaFC(id = 7, valorBpm = 74, hora = "08:00")
    )
    var fcActual = 78
    var pasosActual = 4250
}


