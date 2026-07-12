package mx.utng.smarthealthmonitor.tv.data

import retrofit2.http.*

data class NeonRequest(val query: String, val params: List<Any> = emptyList())

data class NeonResponse<T>(
    val rows        : List<T>   = emptyList(),
    val rowCount    : Int       = 0,
    val command     : String    = "",
)

data class LecturaFcDto(
    val id          : Int    = 0,
    val bpm         : Int,
    val estado      : String,
    val dispositivo : String,
    val hora        : String,
    val fecha       : String  = "",
    val created_at  : String  = "",
)

interface NeonApiService {
    @POST("sql")
    suspend fun executeQuery(
        @Header("Authorization") auth: String,
        @Header("Neon-Connection-String") connStr: String,
        @Body request: NeonRequest
    ): NeonResponse<LecturaFcDto>
}
