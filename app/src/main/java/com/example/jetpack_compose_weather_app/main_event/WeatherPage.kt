package com.example.jetpack_compose_weather_app.main_event


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpack_compose_weather_app.api.NetworkResponse
import com.example.jetpack_compose_weather_app.data.WeatherModel
import com.example.jetpack_compose_weather_app.data.weatherColors
import com.example.jetpack_compose_weather_app.view_model.WeatherViewModel
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.HorizontalDivider
//import com.example.jetpack_compose_weather_app.R


@Composable
fun WeatherDisplay(viewModel: WeatherViewModel, onCountryClick: (String) -> Unit) {
    var city by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    //observe weatherResult state from the viewModel
    val weatherResult = viewModel.weatherResult.observeAsState()

    var isCelsius by remember {
        mutableStateOf(true)
    }

    val backgroundColor = when(val result = weatherResult.value) {
        is NetworkResponse.Success -> weatherColors[result.data.current.condition.code] ?: Color.LightGray
        is NetworkResponse.Error -> Color.White
        else -> Color.White
    } //Default White if can't fetch for colors

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(30.dp))

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

        var showCityList by remember { mutableStateOf(false) }
        val selectedCountry by remember { mutableStateOf<String?>(null) }
        if (showCityList) {
            CityListPage(country = selectedCountry!!) { showCityList = false }
        } else {
            when (val result = weatherResult.value) {
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
                    WeatherDetail(data = result.data, isCelsius = isCelsius, onCountryClick = onCountryClick)
                    Button(onClick = { isCelsius = !isCelsius }) {
                        Text(if (isCelsius) "Switch to Imperial" else "Switch to Metric")
                    }
                }
                // If the result is null, do nothing
                null -> {}
            }

        }

    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListPage(country: String, onClose: () -> Unit) {
    val cityList by remember { mutableStateOf(mutableStateListOf("")) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Manage cities in $country") },
                navigationIcon = {IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { cityList.add("New City ${cityList.size + 1}\"") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add City")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(cityList) { city ->
                CityCard(city = city) { action, cityName ->
                    selectedCity = cityName.toString()
                    if (action == "edit") {
                        // Handle edit action
                    } else if (action == "delete") {
                        showDialog = true
                    }
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete $selectedCity?") },
                confirmButton = {
                    Button(onClick = {
                        cityList.remove(selectedCity)
                        showDialog = false
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}

@Composable
fun CityCard(city: String, onAction: (String, String) -> Unit, data: WeatherModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = city, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Filled.LocationOn, contentDescription = "Location", modifier = Modifier.size(16.dp))
                }

                LaunchedEffect(city) {
                    val weatherData = data.location.name
                }

                Text(text = "${data.current.temp_c}°C", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = data.current.condition.text, fontSize = 14.sp)
            }
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Edit/Delete")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        onAction("edit", city)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onAction("delete", city)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun WeatherDetail(data: WeatherModel, isCelsius: Boolean, onCountryClick: (String) -> Unit) {
    val backgroundColor = weatherColors[data.current.condition.code] ?: Color.LightGray

    val textColor = if (backgroundColor.luminance() > 0.5) Color.Black else Color.White

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
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = data.location.name,
                    fontSize = 30.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = data.location.country, fontSize = 18.sp, color = Color.Gray,
                    modifier = Modifier.clickable{
                        onCountryClick(data.location.country)
                    }
                )
            }


        }
        Spacer(modifier = Modifier.height(8.dp))


        Spacer(modifier = Modifier.height(16.dp))
        Text(text = if (isCelsius) "${data.current.temp_c} °C" else "${data.current.temp_f} °F",
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
            color =  textColor
        )

        Spacer(modifier = Modifier.height(18.dp))
        val detailsList = listOf(
            Pair("Feels like",
                if (isCelsius) "${data.current.feelslike_c} °C" else "${data.current.feelslike_f} °F"
            ),
            Pair("Wind speed",
                if (isCelsius) "${data.current.wind_kph} km/h" else "${data.current.wind_mph} mph"
            ),
            Pair("Humidity", "${data.current.humidity}%"),
            Pair("UV", data.current.uv),
            Pair("Visibility",
                if (isCelsius) "${data.current.vis_km} km" else "${data.current.vis_miles} mi"
            ),
            Pair("Air pressure",
                if (isCelsius) "${data.current.pressure_mb} mb" else "${data.current.pressure_in} in"
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            items(detailsList) { detail ->
                WeatherDetailItem(label = detail.first, value = detail.second, textColor = textColor)
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
fun WeatherDetailItem(label: String, value: String, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(2.dp, textColor, shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 14.sp, color = textColor)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
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




