package edu.mx.cmjg.smarthealthmonitor.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import edu.mx.cmjg.smarthealthmonitor.data.models.LecturaFC

@Dao
interface LecturaFCDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(lectura: LecturaFC): Long

    // Flow: actualización reactiva cuando hay nuevos datos
    @Query("""
        SELECT * FROM lecturas_fc
        ORDER BY timestamp DESC
        LIMIT 50""")  // últimas 50 lecturas
    fun obtenerUltimas(): Flow<List<LecturaFC>>

    @Query("SELECT COUNT(*) FROM lecturas_fc")
    fun contarRegistros(): Int

    // Limpiar lecturas más antiguas de 7 días
    @Query("""
        DELETE FROM lecturas_fc
        WHERE timestamp < :limite""")  
    fun limpiarViejos(limite: Long): Int
}
