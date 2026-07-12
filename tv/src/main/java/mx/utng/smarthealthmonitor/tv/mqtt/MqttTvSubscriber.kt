package mx.utng.smarthealthmonitor.tv.mqtt

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttTvSubscriber(
    private val context: Context,
    private val tvFlow: MutableStateFlow<TvMessage?>
) {
    private var isPolling = false

    fun connect() {
        if (isPolling) return
        isPolling = true
        startPolling()
    }

    private fun startPolling() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isPolling) {
                try {
                    pollLatestTV()
                } catch (e: Exception) {
                    android.util.Log.w("MQTT_TV", "⚠️ Error en polling: ${e.message}")
                }
                delay(5000) // Cada 5 segundos revisamos
            }
        }
    }

    private fun pollLatestTV() {
        val pollId = "tv-poll-${System.currentTimeMillis()}"
        var pollClient: MqttClient? = null

        try {
            pollClient = MqttClient(
                MqttConfig.BROKER_URL,
                pollId,
                MemoryPersistence()
            )

            val options = MqttConnectOptions().apply {
                userName = MqttConfig.USERNAME
                password = MqttConfig.PASSWORD.toCharArray()
                isCleanSession = true
                connectionTimeout = 5
                socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
            }

            pollClient.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String, msg: MqttMessage) {
                    if (topic == MqttConfig.TOPIC_TV) {
                        try {
                            val jsonParser = Json { ignoreUnknownKeys = true }
                            val tvMsg = jsonParser.decodeFromString<TvMessage>(String(msg.payload))
                            tvFlow.value = tvMsg
                            android.util.Log.d("MQTT_TV", "📺 Recibido: ${tvMsg.bpm} bpm")
                        } catch (e: Exception) {
                            android.util.Log.e("MQTT_TV", "❌ Error decodificando TV: ${e.message}")
                        }
                    }
                }
                override fun connectionLost(cause: Throwable?) {}
                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            pollClient.connect(options)
            pollClient.subscribe(MqttConfig.TOPIC_TV, MqttConfig.QOS)
            
            // Esperar brevemente al mensaje retenido
            Thread.sleep(1000)
            
        } catch (e: Exception) {
            android.util.Log.w("MQTT_TV", "⚠️ Poll error: ${e.message}")
        } finally {
            try { pollClient?.disconnect() } catch (_: Exception) {}
            try { pollClient?.close() } catch (_: Exception) {}
        }
    }

    fun disconnect() {
        isPolling = false
    }
}
