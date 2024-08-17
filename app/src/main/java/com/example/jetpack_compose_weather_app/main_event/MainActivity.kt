package com.example.jetpack_compose_weather_app.main_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.jetpack_compose_weather_app.view_model.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MainScreen(weatherViewModel)
        }
    }
}

@Composable
fun MainScreen(viewModel: WeatherViewModel) {
    var showCityList by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf<String?>(null) }

    if (showCityList && selectedCountry != null) {
        CityListPage(country = selectedCountry!!) { showCityList = false }
    } else {
        WeatherDisplay(viewModel = viewModel) { country ->
            selectedCountry = country
            showCityList = true
        }
    }}

