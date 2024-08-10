package com.example.jetpack_compose_weather_app.main_event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpack_compose_weather_app.api.NetworkResponse
import com.example.jetpack_compose_weather_app.api.WeatherModel

@Composable
fun WeatherDisplay(viewModel: WeatherViewModel) {
//    val weatherData by viewModel.weatherData.collectAsState()
    var city by remember {
        mutableStateOf("")
    }

    val weatherResult = viewModel.weatherResult.observeAsState()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = { city = it },
                label = { Text(text = "Enter Location") }
            )
            IconButton(onClick = {
                viewModel.fetchWeather(city)
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search for location")

            }
        }

        when(val result = weatherResult.value) {
            is NetworkResponse.Error -> {
                Text(text = "Error: ${result.message}")
            }
            NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }
            is NetworkResponse.Success -> {
                WeatherDetail(data = result.data)
            }
            null -> {}

        }
    }
}

@Composable
fun WeatherDetail(data: WeatherModel) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp)
            )
            Text(text = data.location.name, fontSize = 30.sp)
            Spacer(modifier = Modifier.width(8.dp) )
            Text(text = data.location.country, fontSize = 18.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "${data.current.temp_c} °C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
            contentDescription = "Weather icon",
        )

        Text(text = data.current.condition.text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color =  Color.Gray
        )
    }
}






















//    Box(modifier = Modifier
//        .fillMaxSize()
//        .paint(
//            painterResource(id = R.drawable.ic_launcher_background),
//            contentScale = ContentScale.FillBounds
//        )) {
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Top
//        ) {
//            Spacer(modifier = Modifier.height(180.dp))
//            OutlinedTextField(value = city,
//                onValueChange = {city = it},
//                label = { Text(text = "City") },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(30.dp),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = Color.White,
//                    unfocusedContainerColor = Color.White,
//                    unfocusedIndicatorColor = Blue1,
//                    focusedLabelColor = DarkBlue1
//                )
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = {viewModel.fetchWeather(city, apiKey)},
//                colors = ButtonDefaults.buttonColors(Blue1)
//            ) {
//                Text(text = "Check Weather")
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//
//            weatherData?.let {
//                Log.d("WeatherData", "Weather data received: $it")
//                Row(modifier =  Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly) {
//                    WeatherCard(label = city, value = it.name, icon = Icons.Default.Place)
//                    WeatherCard(label = "Temperature", value = "${it.main.temp}°C", icon = Icons.Default.Star)
//                }
//                Row(modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly){
//                    WeatherCard(label = "Humidity", value = "${it.main.humidity}%", icon = Icons.Default.Warning)
//                    WeatherCard(label = "Description", value = it.weather[0].description, icon = Icons.Default.Info)
//                }
//            }
//        }
//    }
//}