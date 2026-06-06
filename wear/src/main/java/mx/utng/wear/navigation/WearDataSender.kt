package mx.utng.wear.navigation

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class WearDataSender(private val context: Context) {
    private val messageClient = Wearable.getMessageClient(context)
    private val nodeClient = Wearable.getNodeClient(context)

    suspend fun enviarFC(bpm: Int) {
        try {
            val nodes = nodeClient.connectedNodes.await()
            for (node in nodes) {
                messageClient.sendMessage(
                    node.id,
                    "/smarthealthmonitor/fc",
                    bpm.toString().toByteArray()
                ).await()
            }
            Log.d("WearDataSender", "Sent BPM: $bpm")
        } catch (e: Exception) {
            Log.e("WearDataSender", "Failed to send BPM", e)
        }
    }
}
