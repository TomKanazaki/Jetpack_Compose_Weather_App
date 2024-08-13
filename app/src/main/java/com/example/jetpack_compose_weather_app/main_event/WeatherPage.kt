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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpack_compose_weather_app.api.NetworkResponse
import com.example.jetpack_compose_weather_app.data.WeatherModel
import com.example.jetpack_compose_weather_app.view_model.WeatherViewModel
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.HorizontalDivider
//import com.example.jetpack_compose_weather_app.R
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun WeatherDisplay(viewModel: WeatherViewModel) {
    var city by remember {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    //observe weatherResult state from the viewModel
    val weatherResult = viewModel.weatherResult.observeAsState()

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(16.dp))

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
                label = { Text(text = "Enter Location") },
//                keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Text),
//                keyboardActions = KeyboardActions(
//                    onSearch = {
//                        viewModel.fetchWeather(city)
//                        keyboardController?.hide()
//                    }
//                )
            )
            IconButton(onClick = {
                viewModel.fetchWeather(city)
                keyboardController?.hide()
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search for location")
            }
        }

        when(val result = weatherResult.value) {
            // If these is error, the retry button will show up below the error message
            is NetworkResponse.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: ${result.message}")
                    Button(onClick = { viewModel.fetchWeather(city) }) {
                        Text("Retry")
                    }
                }
            }

            // Display the circular progress indicator if the result is loading
            NetworkResponse.Loading -> {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            }

            // Display the weather details if the result is successful
            is NetworkResponse.Success -> {
                WeatherDetail(data = result.data)
            }
            // If the result is null, do nothing
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

        Spacer(modifier = Modifier.height(18.dp))

        val detailsList = listOf(
            Pair("Feels like", "${data.current.feelslike_c} °C"),
            Pair("Wind speed", "${data.current.wind_kph} km/h"),
            Pair("Humidity", "${data.current.humidity}%"),
            Pair("UV", data.current.uv),
            Pair("Visibility", "${data.current.vis_km} km"),
            Pair("Air pressure", "${data.current.pressure_mb} mb")
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            items(detailsList) { detail ->
                WeatherDetailItem(label = detail.first, value = detail.second)
            }
        }

//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Sunrise & Sunset Card
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 8.dp),
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                SunriseSunsetItem(
//                    icon = R.drawable.sunrise, // Replace with a suitable sunrise icon
//                    time = data.sunrise ?: "--:--" // Replace with actual sunrise time from data
//                )
//                HorizontalDivider(
//                    modifier = Modifier.padding(vertical = 8.dp),
//                    color = Color.LightGray
//                )
//                SunriseSunsetItem(
//                    icon = R.drawable.sunset, // Replace with a suitable sunset icon
//                    time = data.sunset ?: "--:--" // Replace with actual sunset time from data
//                )
//            }
//        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

//@Composable
//fun SunriseSunsetItem(icon: ImageVector, time: String) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = null,
//            modifier = Modifier.size(24.dp)
//        )
//        Spacer(modifier = Modifier.width(8.dp))
//        Text(text = time, fontSize = 16.sp)
//    }
//}

//@SuppressLint("PermissionAPI")
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun WeatherScreen() {
//    val context = LocalContext.current
//    var userLocation by remember { mutableStateOf<String?>(null) }
//    val locationPermissionState = rememberPermissionState(
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    )
//
//    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//    LaunchedEffect(key1 = locationPermissionState.hasPermission) {
//        if (locationPermissionState.hasPermission) {
//            // Get location using addOnSuccessListener
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return@LaunchedEffect
//            }
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                location?.let {
//                    val address = getAddressFromLocation(
//                        context,
//                        it.latitude,
//                        it.longitude
//                    )
//                    userLocation = address // Update the userLocation state
//                }
//            }
//        } else {
//            locationPermissionState.launchPermissionRequest()
//        }
//    }
//
//    // ... Rest of your WeatherScreen Composable
//    Text(text = "Your Location: ${userLocation ?: "Loading..."}")
//    // ...
//}
//
//@SuppressLint("MissingPermission")
//fun getUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient, onLocationFetched: (String) -> Unit) {
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val location: Location? = fusedLocationClient.lastLocation.await()
//            location?.let {
//                val address = getAddressFromLocation(
//                    context,
//                    it.latitude,
//                    it.longitude
//                )
//                onLocationFetched(address)
//            }
//        } catch (e: Exception) {
//            // Handle location retrieval error
//            e.printStackTrace()
//        }
//    }
//}
//
//fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
//    val geocoder = Geocoder(context, Locale.getDefault())
//    try {
//        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//        if (!addresses.isNullOrEmpty()) {
//            val address = addresses[0]
//            val city = address.locality ?: ""
//            val country = address.countryName ?: ""
//            return "$city, $country"
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    return ""
//}

@Preview(showBackground = true)
@Composable
fun WeatherDisplayPreview() {
    WeatherDisplay(viewModel = WeatherViewModel())
}


