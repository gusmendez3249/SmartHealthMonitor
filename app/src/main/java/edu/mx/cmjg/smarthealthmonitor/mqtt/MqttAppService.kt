package edu.mx.cmjg.smarthealthmonitor.mqtt

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.text.SimpleDateFormat
import java.util.Date
import edu.mx.cmjg.smarthealthmonitor.data.SmartHealthRepository

class MqttAppService(
    private val context: Context
) {
    private var lastBpm = -1  // Para evitar re-procesar el mismo valor

    fun connect() {
        // Arrancar el polling cada 5 segundos
        startPolling()
    }

    /**
     * Cada 5 segundos: crear un cliente NUEVO, conectar, suscribirse,
     * recibir el último mensaje retenido, procesarlo, desconectar.
     * Es como ir a revisar el buzón periódicamente.
     */
    private fun startPolling() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    pollLatestFC()
                } catch (e: Exception) {
                    android.util.Log.w("MQTT_APP", "⚠️ Error en polling: ${e.message}")
                }
                delay(5000) // Cada 5 segundos
            }
        }
    }

    private fun pollLatestFC() {
        // Cliente con ID único para cada poll
        val pollId = "poll-${System.currentTimeMillis()}"
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

            // Callback para capturar el mensaje retenido
            pollClient.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String, msg: MqttMessage) {
                    handleFcMessage(msg)
                }
                override fun connectionLost(cause: Throwable?) {}
                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            // Conectar (síncrono, bloqueante)
            pollClient.connect(options)

            // Suscribirse — el broker entrega el mensaje retenido al instante
            pollClient.subscribe(MqttConfig.TOPIC_FC, MqttConfig.QOS)

            // Esperar un momento para que el mensaje llegue
            Thread.sleep(1000)

            android.util.Log.d("MQTT_APP", "🔄 Poll completado")

        } catch (e: Exception) {
            android.util.Log.w("MQTT_APP", "⚠️ Poll error: ${e.message}")
        } finally {
            // Siempre desconectar y cerrar
            try { pollClient?.disconnect() } catch (_: Exception) {}
            try { pollClient?.close() } catch (_: Exception) {}
        }
    }

    private fun handleFcMessage(msg: MqttMessage) {
        try {
            val payloadString = String(msg.payload)
            val jsonParser = Json { ignoreUnknownKeys = true }
            val fcMsg = jsonParser.decodeFromString<FcMessage>(payloadString)

            // Solo procesar si el valor cambió
            if (fcMsg.bpm == lastBpm) return
            lastBpm = fcMsg.bpm

            android.util.Log.d("MQTT_APP", "📩 Nuevo valor: ${fcMsg.bpm} bpm (estado: ${fcMsg.estado})")

            // 1. Actualizar el Repository y BD
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    SmartHealthRepository.actualizarFC(fcMsg.bpm)
                } catch (e: Exception) {
                    android.util.Log.e("MQTT_APP", "❌ Error BD: ${e.message}")
                }
            }

            // 2. Re-publicar al topic TV con un cliente efímero
            CoroutineScope(Dispatchers.IO).launch {
                publishToTV(fcMsg.bpm, fcMsg.estado)
            }

        } catch (e: Exception) {
            android.util.Log.e("MQTT_APP", "❌ Error: ${e.message}", e)
        }
    }

    private fun publishToTV(bpm: Int, estado: String) {
        var tvClient: MqttClient? = null
        try {
            tvClient = MqttClient(
                MqttConfig.BROKER_URL,
                "tv-pub-${System.currentTimeMillis()}",
                MemoryPersistence()
            )
            val options = MqttConnectOptions().apply {
                userName = MqttConfig.USERNAME
                password = MqttConfig.PASSWORD.toCharArray()
                isCleanSession = true
                connectionTimeout = 5
                socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
            }
            tvClient.connect(options)

            val jsonParser = Json { ignoreUnknownKeys = true }
            val hora = SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(Date())
            val tvMsg = TvMessage(bpm = bpm, estado = estado, hora = hora)
            val payload = jsonParser.encodeToString(tvMsg).toByteArray()
            val mqttMsg = MqttMessage(payload).apply {
                qos = MqttConfig.QOS
                isRetained = true
            }
            tvClient.publish(MqttConfig.TOPIC_TV, mqttMsg)
            android.util.Log.d("MQTT_APP", "🔁 Re-publicado al TV: $bpm bpm")

        } catch (e: Exception) {
            android.util.Log.e("MQTT_APP", "❌ Error publicando TV: ${e.message}")
        } finally {
            try { tvClient?.disconnect() } catch (_: Exception) {}
            try { tvClient?.close() } catch (_: Exception) {}
        }
    }

    fun disconnect() {
        // Nada que desconectar, los clientes de poll se cierran solos
    }
}
