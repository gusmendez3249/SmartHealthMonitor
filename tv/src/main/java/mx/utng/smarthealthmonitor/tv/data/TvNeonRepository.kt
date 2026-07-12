package mx.utng.smarthealthmonitor.tv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TvNeonRepository {

    /** Obtener historial completo de los 3 dispositivos */
    suspend fun obtenerHistorialCompleto(limite: Int = 50): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT id,bpm,estado,dispositivo,hora,created_at
                               FROM lecturas_fc
                               ORDER BY created_at DESC
                               LIMIT $1""".trimIndent(),
                    params = listOf(limite)
                )
            ).rows
        }

    /** Estadísticas por dispositivo */
    suspend fun obtenerEstadisticas(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT dispositivo,
                               ROUND(AVG(bpm)) AS bpm,
                               'Promedio' AS estado,
                               MAX(hora) AS hora
                               FROM lecturas_fc
                               GROUP BY dispositivo""".trimIndent()
                )
            ).rows
        }
 
    /** Alertas de FC fuera de rango (últimas 24 horas) */
    suspend fun obtenerAlertas(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT * FROM lecturas_fc
                               WHERE (bpm < 60 OR bpm > 100)
                                 AND created_at > NOW() - INTERVAL '24 hours'
                               ORDER BY created_at DESC;""".trimIndent()
                )
            ).rows
        }
 
    /** Promedio de FC por hora del día */
    suspend fun obtenerPromedioHora(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT EXTRACT(HOUR FROM created_at) AS hora_dia,
                                      ROUND(AVG(bpm)) AS promedio_bpm,
                                      COUNT(*) AS total_lecturas
                               FROM lecturas_fc
                               GROUP BY hora_dia
                               ORDER BY hora_dia;""".trimIndent()
                )
            ).rows
        }
 
    /** Lectura más reciente de cada dispositivo */
    suspend fun obtenerLecturaMasRecientePorDispositivo(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT DISTINCT ON (dispositivo)
                                      dispositivo, bpm, estado, hora, created_at
                               FROM lecturas_fc
                               ORDER BY dispositivo, created_at DESC;""".trimIndent()
                )
            ).rows
        }
 
    /** Detección de taquicardia sostenida */
    suspend fun obtenerTaquicardiaSostenida(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT COUNT(*) AS lecturas_altas,
                                      MIN(hora) AS desde, MAX(hora) AS hasta
                               FROM lecturas_fc
                               WHERE bpm > 100
                                 AND created_at > NOW() - INTERVAL '1 hour';""".trimIndent()
                )
            ).rows
        }
}
