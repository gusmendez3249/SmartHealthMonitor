package mx.utng.smarthealthmonitor.wear.mqtt

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttWearPublisher(private val context: Context) {

    fun connect() {
        // Ya no necesitamos mantener una conexión viva (persistent connection).
        // Cada publicación (publishFC) abrirá su propia conexión efímera síncrona,
        // lo que es mucho más seguro en Wear OS y evita bloqueos de hilos de red.
    }

    /** Publicar FC al topic MQTT */
    fun publishFC(bpm: Int, estado: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var client: MqttClient? = null
            try {
                // ID único para no chocar con conexiones anteriores zombies
                val clientId = "${MqttConfig.CLIENT_WEAR}-${System.currentTimeMillis()}"
                
                client = MqttClient(
                    MqttConfig.BROKER_URL,
                    clientId,
                    MemoryPersistence()
                )

                val options = MqttConnectOptions().apply {
                    userName = MqttConfig.USERNAME
                    password = MqttConfig.PASSWORD.toCharArray()
                    isCleanSession = true
                    connectionTimeout = 5
                    socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
                }

                client.connect(options)

                val message = FcMessage(bpm = bpm, estado = estado)
                val payload = Json.encodeToString(message).toByteArray()

                val mqttMessage = MqttMessage(payload).apply {
                    qos      = MqttConfig.QOS
                    isRetained = true  // el TV o el App verán el último valor
                }

                client.publish(MqttConfig.TOPIC_FC, mqttMessage)
                android.util.Log.d("MQTT_WEAR", "📤 Publicado efímero: $bpm bpm → ${MqttConfig.TOPIC_FC}")

            } catch (e: Exception) {
                android.util.Log.e("MQTT_WEAR", "❌ Error publicando efímero: ${e.message}")
            } finally {
                try { client?.disconnect() } catch (_: Exception) {}
                try { client?.close() } catch (_: Exception) {}
            }
        }
    }

    fun disconnect() {
        // Nada que desconectar, la arquitectura es sin estado (stateless) ahora
    }
}
