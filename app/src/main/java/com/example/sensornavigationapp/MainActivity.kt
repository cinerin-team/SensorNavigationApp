package com.example.sensornavigationapp

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ComponentActivity
import androidx.compose.runtime.Composable


@SuppressLint("RestrictedApi")
class MainActivity : ComponentActivity() {
    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: error("No Accelerometer found")
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) ?: error("No Magnetometer found")

        setContent {
            MaterialTheme {
                Greeting("Android")
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello, $name!")
}

@Composable
fun SensorNavigationApp(
    sensorManager: SensorManager,
    accelerometer: Sensor,
    magnetometer: Sensor
) {
    var distance by remember { mutableStateOf(0.0) }
    var isMeasuring by remember { mutableStateOf(false) }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isMeasuring) {
                    // Itt frissítjük a távolságot a gyorsulásmérőből és mágneses mező adatokból
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        // Gyorsulásmérő adatainak feldolgozása (időbeli integrálás távolságra)
                        distance += processAccelerometerData(event)
                    }
                    if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        // Mágneses mező adatok kezelése, irányítási korrekció
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    LaunchedEffect(isMeasuring) {
        if (isMeasuring) {
            sensorManager.registerListener(
                sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL
            )
            sensorManager.registerListener(
                sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL
            )
        } else {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Distance: ${"%.2f".format(distance)} meters")

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                isMeasuring = true
                distance = 0.0 // Reset distance on start
            }) {
                Text("Start")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { isMeasuring = false }) {
                Text("Stop")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                isMeasuring = false
                distance = 0.0
            }) {
                Text("Reset")
            }
        }
    }
}

fun processAccelerometerData(event: SensorEvent): Double {
    // Gyorsulásmérő adatainak feldolgozása, például egyszerű integrálással.
    // Itt Kalman-szűrőt alkalmazhatsz a zajok kiszűrésére.
    return event.values[0].toDouble() // Ezt módosítsd valós integrálásra
}