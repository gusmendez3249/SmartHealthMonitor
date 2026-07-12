package mx.utng.smarthealthmonitor.tv.data

import retrofit2.http.*

data class NeonRequest(val query: String, val params: List<Any> = emptyList())

data class NeonResponse<T>(
    val rows        : List<T>   = emptyList(),
    val rowCount    : Int       = 0,
    val command     : String    = "",
)

data class LecturaFcDto(
    val id             : Int    = 0,
    val bpm            : Int    = 0,
    val estado         : String = "",
    val dispositivo    : String = "",
    val hora           : String = "",
    val fecha          : String = "",
    val created_at     : String = "",
    
    // Extra fields for advanced queries
    val hora_dia       : Double? = null,
    val promedio_bpm   : Double? = null,
    val total_lecturas : Int? = null,
    val lecturas_altas : Int? = null,
    val desde          : String? = null,
    val hasta          : String? = null
)

interface NeonApiService {
    @POST("sql")
    suspend fun executeQuery(
        @Header("Neon-Connection-String") connStr: String,
        @Body request: NeonRequest
    ): NeonResponse<LecturaFcDto>
}
