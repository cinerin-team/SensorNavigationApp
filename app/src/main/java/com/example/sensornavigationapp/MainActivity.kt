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
import androidx.activity.ComponentActivity
import kotlin.math.pow
import kotlin.math.sqrt

@SuppressLint("RestrictedApi")
class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor
    private var previousTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: error("No Accelerometer found")
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) ?: error("No Magnetometer found")

        setContent {
            MaterialTheme {
                SensorNavigationApp(sensorManager, accelerometer, magnetometer)
            }
        }
    }
}

@Composable
fun SensorNavigationApp(
    sensorManager: SensorManager,
    accelerometer: Sensor,
    magnetometer: Sensor
) {
    var distance by remember { mutableStateOf(0.0) }
    var isMeasuring by remember { mutableStateOf(false) }
    var previousVelocity by remember { mutableStateOf(0.0) }
    var previousDistance by remember { mutableStateOf(0.0) }
    var previousTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isMeasuring) {
                    val currentTime = System.currentTimeMillis()
                    val deltaTime = (currentTime - previousTime) / 1000f // Convert ms to seconds
                    previousTime = currentTime

                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        distance = processAccelerometerData(event, deltaTime, previousVelocity, previousDistance)
                        previousDistance = distance
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    LaunchedEffect(isMeasuring) {
        if (isMeasuring) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
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

fun processAccelerometerData(
    event: SensorEvent,
    deltaTime: Float,
    previousVelocity: Double,
    previousDistance: Double
): Double {
    // Calculate the magnitude of the acceleration vector
    val acceleration = sqrt(
        event.values[0].pow(2) +
                event.values[1].pow(2) +
                event.values[2].pow(2)
    )
    // Update velocity using acceleration and time (v = u + at)
    val velocity = previousVelocity + (acceleration * deltaTime)
    // Update distance using velocity and time (d = v * t)
    return previousDistance + (velocity * deltaTime)
}
