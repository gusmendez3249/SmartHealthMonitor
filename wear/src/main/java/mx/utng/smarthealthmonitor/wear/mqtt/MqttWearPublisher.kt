package mx.utng.smarthealthmonitor.wear.mqtt

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttWearPublisher(private val context: Context) {

    private var client: MqttAsyncClient? = null
    private var connectOptions: MqttConnectOptions? = null

    fun connect() {
        client = MqttAsyncClient(
            MqttConfig.BROKER_URL,
            MqttConfig.CLIENT_WEAR,
            MemoryPersistence()
        )

        connectOptions = MqttConnectOptions().apply {
            userName        = MqttConfig.USERNAME
            password        = MqttConfig.PASSWORD.toCharArray()
            isCleanSession  = true
            isAutomaticReconnect = true
            connectionTimeout = 10
            keepAliveInterval = 15
            socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
        }

        doConnect()
        startConnectionMonitor()
    }

    private fun doConnect() {
        try {
            client?.connect(connectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    android.util.Log.d("MQTT_WEAR", "✅ Conectado a HiveMQ Cloud")
                }
                override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                    android.util.Log.e("MQTT_WEAR", "❌ Error conectando: ${ex?.message}")
                }
            })
        } catch (e: Exception) {
            android.util.Log.e("MQTT_WEAR", "❌ Error en doConnect: ${e.message}")
        }
    }

    /** Publicar FC al topic MQTT */
    fun publishFC(bpm: Int, estado: String) {
        // Si está desconectado, intentar reconectar de inmediato
        if (client?.isConnected != true) {
            android.util.Log.w("MQTT_WEAR", "⚠️ Desconectado. Intentando reconectar para publicar $bpm bpm...")
            try {
                client?.reconnect()
            } catch (_: Exception) {}
            // Esperar brevemente a que reconecte (máx 2 segundos)
            var intentos = 0
            while (client?.isConnected != true && intentos < 20) {
                Thread.sleep(100)
                intentos++
            }
            if (client?.isConnected != true) {
                android.util.Log.e("MQTT_WEAR", "❌ No se pudo reconectar para publicar $bpm bpm")
                return
            }
            android.util.Log.d("MQTT_WEAR", "🔄 Reconectado exitosamente!")
        }

        val message = FcMessage(bpm = bpm, estado = estado)
        val payload = Json.encodeToString(message).toByteArray()

        val mqttMessage = MqttMessage(payload).apply {
            qos      = MqttConfig.QOS
            isRetained = true  // el TV verá el último valor al conectarse
        }

        try {
            client?.publish(MqttConfig.TOPIC_FC, mqttMessage)
            android.util.Log.d("MQTT_WEAR", "📤 Publicado: $bpm bpm → ${MqttConfig.TOPIC_FC}")
        } catch (e: Exception) {
            android.util.Log.e("MQTT_WEAR", "❌ Error publicando: ${e.message}")
        }
    }

    private fun startConnectionMonitor() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(5000) // Revisa cada 5 segundos
                if (client != null && client?.isConnected == false) {
                    android.util.Log.w("MQTT_WEAR", "⚠️ Monitor detectó desconexión. Forzando reconexión...")
                    try {
                        client?.reconnect()
                    } catch (_: Exception) {}
                }
            }
        }
    }

    fun disconnect() { client?.disconnect() }
}
