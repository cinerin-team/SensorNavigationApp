package com.example.sensornavigationapp

import android.content.Context
import android.hardware.*
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: error("No Accelerometer found")
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) ?: error("No Magnetometer found")

        setContent {
            MaterialTheme {
                // Call the SensorNavigationApp function
                SensorNavigationApp(
                    sensorManager = sensorManager,
                    accelerometer = accelerometer,
                    magnetometer = magnetometer
                )
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
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Testing the UI layout!")
    }
}
