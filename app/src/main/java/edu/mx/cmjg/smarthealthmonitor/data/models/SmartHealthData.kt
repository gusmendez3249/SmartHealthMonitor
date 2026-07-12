package edu.mx.cmjg.smarthealthmonitor.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecturas_fc")
data class LecturaFC(
    @PrimaryKey(autoGenerate = true)
    val id           : Int     = 0,
    val bpm          : Int,
    val estado       : String,
    val dispositivo  : String  = "app",  // wear | app | tv
    val hora         : String = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
    @androidx.room.ColumnInfo(name = "sincronizado")
    val sincronizado : Boolean = false   // false = pendiente de sync
)

// Datos de prueba para desarrollo (mock data)
object MockData {
    val historialFC = listOf(
        LecturaFC(id = 1, bpm = 78, estado = "Normal", hora = "11:00"),
        LecturaFC(id = 2, bpm = 82, estado = "Normal", hora = "10:30"),
        LecturaFC(id = 3, bpm = 76, estado = "Normal", hora = "10:00"),
        LecturaFC(id = 4, bpm = 95, estado = "FC Alta", hora = "09:30"),
        LecturaFC(id = 5, bpm = 71, estado = "Normal", hora = "09:00"),
        LecturaFC(id = 6, bpm = 80, estado = "Normal", hora = "08:30"),
        LecturaFC(id = 7, bpm = 74, estado = "Normal", hora = "08:00")
    )
    var fcActual = 78
    var pasosActual = 4250
}


