package edu.mx.cmjg.smarthealthmonitor
 
import android.content.Context
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.CastMediaControlIntent
 
class SmartHealthCastOptionsProvider : OptionsProvider {
 
    override fun getCastOptions(context: Context): CastOptions {
        return CastOptions.Builder()
            // DEFAULT_MEDIA_RECEIVER: el receptor genérico de Google
            // Para producción: registrar tu propio App ID en
            // cast.google.com/publish
            .setReceiverApplicationId(
                CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID
            )
            .build()
    }
 
    override fun getAdditionalSessionProviders(ctx: Context): List<SessionProvider>? = null
}
