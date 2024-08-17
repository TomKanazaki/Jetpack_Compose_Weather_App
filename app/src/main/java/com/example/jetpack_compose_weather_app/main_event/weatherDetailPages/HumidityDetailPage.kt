package com.example.jetpack_compose_weather_app.main_event.weatherDetailPages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumidityDetailPage(dewpointC: String? = "N/A", dewpointF: String? = "N/A", onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Humidity Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (dewpointC != null && dewpointF != null) {
                Text("Dewpoint (Celsius): $dewpointC °C", fontSize = 18.sp)
                Text("Dewpoint (Fahrenheit): $dewpointF °F", fontSize = 18.sp)
            } else {
                Text("Dewpoint data not available", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "About Relative Humidity:\n\n" +
                            "Relative humidity, commonly known just as humidity, is the amount of moisture in the air compared to what the air can hold. " +
                            "The air can hold more moisture at higher temperatures. A relative humidity near 100% means there may be dew or fog.\n\n" +
                            "About the Dew Point:\n\n" +
                            "The dew point is what the temperature would need to fall to for dew to form. " +
                            "It can be a useful way to tell how humid the air feels - the higher the dew point, the more humid it feels. " +
                            "A dew point that matches the current temperature means the relative humidity is 100%, and there may be dew or fog.",
                    fontSize = 16.sp
                )
            }}
    }
}