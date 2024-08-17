package com.example.jetpack_compose_weather_app.main_event

import android.annotation.SuppressLint
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

@SuppressLint("MutableCollectionMutableState")
@Composable
fun MainScreen(viewModel: WeatherViewModel) {
    var showCityList by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf<String?>(null) }
    var currentCity by remember { mutableStateOf("") }
    val cityList by remember { mutableStateOf(mutableListOf<String>()) }

    if (showCityList && selectedCountry != null) {
        CityListPage(
            viewModel = viewModel,
            onClose = { showCityList = false },
            country = selectedCountry!!,
            cityList = cityList,
        )
    } else {
        WeatherDisplay(viewModel = viewModel) { country, city ->
            selectedCountry = country
            currentCity = city
            showCityList = true

        }
    }}
