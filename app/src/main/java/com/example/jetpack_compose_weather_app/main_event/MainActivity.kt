package com.example.jetpack_compose_weather_app.main_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            WeatherDisplay(weatherViewModel)
        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun WeatherPreview() {
//    Jetpack_Compose_Weather_AppTheme {
//        WeatherDisplay()
//    }
//}