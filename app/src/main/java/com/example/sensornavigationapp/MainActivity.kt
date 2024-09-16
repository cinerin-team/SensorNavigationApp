package com.example.sensornavigationapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializing sensors
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            ?: error("No Accelerometer found")
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            ?: error("No Magnetometer found")

        setContent {
            SensorNavigationApp(sensorManager, accelerometer, magnetometer)
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

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isMeasuring) {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        distance += processAccelerometerData(event)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    LaunchedEffect(isMeasuring) {
        if (isMeasuring) {
            sensorManager.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            sensorManager.registerListener(
                sensorEventListener,
                magnetometer,
                SensorManager.SENSOR_DELAY_NORMAL
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
    // Example processing: Convert acceleration to distance (this is simplified)
    val acceleration = event.values[0].toDouble() // Use X-axis for simplicity
    // Process the acceleration values to compute distance (you can use integration)
    // This is a placeholder; you may need Kalman filtering or other logic
    return acceleration * 0.1 // Example scaling factor
}