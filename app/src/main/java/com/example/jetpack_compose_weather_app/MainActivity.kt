package com.example.jetpack_compose_weather_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpack_compose_weather_app.ui.theme.Blue1
import com.example.jetpack_compose_weather_app.ui.theme.DarkBlue1
import com.example.jetpack_compose_weather_app.ui.theme.Jetpack_Compose_Weather_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherDisplay()
        }
    }
}

@Composable
fun WeatherDisplay() {
    val viewModel: WeatherViewModel = viewModel()
    val weatherData by viewModel.weatherData.collectAsState()
    var city by remember {
        mutableStateOf("")
    }
    val apiKey = "a935eacce4344ec42ecfed86a0c4a860"
    
    Box(modifier = Modifier
        .fillMaxSize()
        .paint(
            painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.FillBounds
        )) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(180.dp))
            OutlinedTextField(value = city,
                onValueChange = {city = it},
                label = {Text(text = "City")},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Blue1,
                    focusedLabelColor = DarkBlue1
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {viewModel.fetchWeather(city, apiKey)},
                colors = ButtonDefaults.buttonColors(Blue1)
            ) {
                Text(text = "Check Weather")
            }
            Spacer(modifier = Modifier.height(16.dp))

            weatherData?.let {
                Log.d("WeatherData", "Weather data received: $it")
                Row(modifier =  Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    WeatherCard(label = city, value = it.name, icon = Icons.Default.Place)
                    WeatherCard(label = "Temperature", value = "${it.main.temp}°C", icon = Icons.Default.Star)
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly){
                    WeatherCard(label = "Humidity", value = "${it.main.humidity}%", icon = Icons.Default.Warning)
                    WeatherCard(label = "Description", value = it.weather[0].description, icon = Icons.Default.Info)
                }
            }
        }
    }
}

@Composable
fun WeatherCard(label: String, value: String, icon: ImageVector) {
    Card(modifier = Modifier
        .padding(8.dp)
        .size(150.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(imageVector = icon, contentDescription = null,
                    tint = DarkBlue1,
                    modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 14.sp, color = DarkBlue1)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f),
            contentAlignment = Alignment.Center) {
            Text(text = value, fontSize = 22.sp, color = DarkBlue1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)

        }

    }
}

@Preview(showBackground = true)
@Composable
fun WeatherPreview() {
    Jetpack_Compose_Weather_AppTheme {
        WeatherDisplay()
    }
}