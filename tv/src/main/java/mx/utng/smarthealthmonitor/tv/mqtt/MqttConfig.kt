package mx.utng.smarthealthmonitor.tv.mqtt

import mx.utng.smarthealthmonitor.tv.BuildConfig

object MqttConfig {
    const val BROKER_URL  = "ssl://${BuildConfig.MQTT_BROKER_URL}:${BuildConfig.MQTT_PORT}"
    const val USERNAME    = BuildConfig.MQTT_USERNAME
    const val PASSWORD    = BuildConfig.MQTT_PASSWORD

    // Topics del proyecto
    const val TOPIC_FC    = "utng/smarthealthmonitor/fc"
    const val TOPIC_TV    = "utng/smarthealthmonitor/tv"
    const val TOPIC_ALERT = "utng/smarthealthmonitor/alerta"

    // QoS: 0=best effort, 1=at least once, 2=exactly once
    const val QOS = 1

    // Client IDs únicos por dispositivo
    const val CLIENT_WEAR = "smarthealthmonitor-wear"
    const val CLIENT_APP  = "smarthealthmonitor-app"
    const val CLIENT_TV   = "smarthealthmonitor-tv"
}
