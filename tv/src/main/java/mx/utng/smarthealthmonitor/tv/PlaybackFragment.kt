package mx.utng.smarthealthmonitor.tv
 
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.app.PlaybackSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
 
class PlaybackFragment : PlaybackSupportFragment() {
 
    private lateinit var player: ExoPlayer
 
    companion object {
        private const val UPDATE_DELAY_MS = 16
        const val ARG_URL = "media_url"
        const val ARG_TITLE = "media_title"
 
        fun newInstance(url: String, title: String = "Alerta"): PlaybackFragment =
            PlaybackFragment().apply {
                arguments = Bundle().also {
                    it.putString(ARG_URL, url)
                    it.putString(ARG_TITLE, title)
                }
            }
    }
 
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url   = arguments?.getString(ARG_URL)   ?: return
        val title = arguments?.getString(ARG_TITLE) ?: ""
 
        // 1. Crear el motor de reproducción de Media3
        player = ExoPlayer.Builder(requireContext()).build()
 
        // 2. Conectar con la UI de Leanback usando el adaptador de Media3
        val adapter = LeanbackPlayerAdapter(
            requireContext(), player, UPDATE_DELAY_MS
        )
        val glue = PlaybackTransportControlGlue(requireContext(), adapter).apply {
            this.title    = title
            this.subtitle = "SmartHealth Monitor"
            host = PlaybackSupportFragmentGlueHost(this@PlaybackFragment)
            playWhenPrepared()
        }
 
        // 3. Cargar y reproducir el media
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()

        // ⭐ Reto adicional: Registrar duración (Simulado ya que no tenemos acceso a Room aquí)
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    val durationSeconds = player.duration / 1000
                    Toast.makeText(context, "Se reprodujeron $durationSeconds s", Toast.LENGTH_SHORT).show()
                    Log.d("PlaybackFragment", "Duración simulada guardada en Room: $durationSeconds segundos")
                }
            }
        })
    }
 
    // ⚠️ SIEMPRE liberar ExoPlayer — error crítico olvidarlo
    override fun onDestroyView() {
        super.onDestroyView()
        // Mostrar también en destroy cuánto tiempo lo escuchó si no terminó
        if (::player.isInitialized) {
            val listenedSeconds = player.currentPosition / 1000
            Toast.makeText(context, "Registro (Simulado): $listenedSeconds s", Toast.LENGTH_SHORT).show()
            player.release()
        }
    }
}
