package edu.mx.cmjg.smarthealthmonitor.cast
 
import android.content.Context
import android.net.Uri
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastContext
import org.json.JSONObject
 
object CastManager {
 
    // Namespace del canal Custom Cast
    // Debe coincidir con el namespace del Web Receiver
    const val CAST_NAMESPACE = "urn:x-cast:mx.utng.smarthealthmonitor"
 
    /**
     * Envía la FC actual al receptor Cast via Custom Channel.
     * El receptor debe escuchar en CAST_NAMESPACE.
     */
    fun enviarFC(context: Context, bpm: Int) {
        val castSession = getCastSession(context) ?: return
        val mensaje = JSONObject().apply {
            put("type", "FC_UPDATE")
            put("bpm", bpm)
            put("esNormal", bpm in 60..100)
            put("timestamp", System.currentTimeMillis())
        }.toString()
 
        castSession.sendMessage(CAST_NAMESPACE, mensaje)
            .setResultCallback { status ->
                if (!status.isSuccess) {
                    android.util.Log.w("Cast",
                        "Error enviando FC: ${status.statusMessage}")
                }
            }
    }
 
    /**
     * Carga y reproduce un media URL en el receptor Cast.
     * La TV reproduce independientemente del teléfono.
     */
    fun reproducirEnTV(context: Context, url: String, titulo: String) {
        val castSession = getCastSession(context) ?: return
        val metadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_GENERIC).apply {
            putString(MediaMetadata.KEY_TITLE, titulo)
            putString(MediaMetadata.KEY_SUBTITLE, "SmartHealth Monitor")
        }
        val mediaInfo = MediaInfo.Builder(url)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setMetadata(metadata)
            .build()
        val request = MediaLoadRequestData.Builder()
            .setMediaInfo(mediaInfo)
            .setAutoplay(true)
            .build()
        castSession.remoteMediaClient?.load(request)
    }
 
    /** Verifica si hay una sesión Cast activa */
    fun isConnected(context: Context): Boolean =
        getCastSession(context) != null
 
    private fun getCastSession(context: Context) =
        try {
            CastContext.getSharedInstance(context)
                .sessionManager.currentCastSession
        } catch (e: Exception) { null }
}
