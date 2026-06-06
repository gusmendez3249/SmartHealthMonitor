package mx.utng.wear


import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import android.util.Log
import kotlinx.coroutines.launch
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import mx.utng.wear.navigation.WearDataSender

class WearMainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var statusText: TextView
    private lateinit var wearDataSender: WearDataSender

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val bodySensorsGranted = permissions[Manifest.permission.BODY_SENSORS] == true
        val activityRecGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true
        val healthHeartRateGranted = permissions["android.permission.health.READ_HEART_RATE"] == true
        
        // Dependiendo de la versión de Wear OS, se otorga BODY_SENSORS o READ_HEART_RATE.
        val hasHeartRatePermission = bodySensorsGranted || healthHeartRateGranted

        if (hasHeartRatePermission && activityRecGranted) {
            statusText.text = "¡Servicio activo!\nEscuchando pulsaciones..."
            statusText.setTextColor(Color.GREEN)
            val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

            if (heartRateSensor != null) {
                sensorManager.registerListener(this@WearMainActivity, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST)
            } else {
                statusText.text = "Error:\nSensor no disponible"
                statusText.setTextColor(Color.RED)
            }
        } else {
            statusText.text = "Denegado:\nSensors: $hasHeartRatePermission\nActivity: $activityRecGranted"
            statusText.setTextColor(Color.RED)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        wearDataSender = WearDataSender(this)

        // Crear una vista simple para evitar la pantalla negra
        statusText = TextView(this).apply {
            text = "Solicitando permisos..."
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            textSize = 16f
        }
        setContentView(statusText)

        // Request required permissions before registering the listener
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACTIVITY_RECOGNITION,
                "android.permission.health.READ_HEART_RATE"
            )
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            val values = event.values
            if (values.isNotEmpty()) {
                val bpm = values[0].toInt()
                if (bpm > 0) {
                    Log.d("WearMainActivity", "Read Raw BPM: $bpm")
                    lifecycleScope.launch {
                        statusText.text = "BPM: $bpm"
                        statusText.setTextColor(if (bpm < 110) Color.BLUE else Color.RED)
                        wearDataSender.enviarFC(bpm)
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
