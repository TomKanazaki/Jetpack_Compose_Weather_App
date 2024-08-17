package com.example.jetpack_compose_weather_app.main_event

import UVDetailPage
import VisibilityDetailPage
import WindDetailPage
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.jetpack_compose_weather_app.main_event.weatherDetailPages.HumidityDetailPage
import com.example.jetpack_compose_weather_app.view_model.WeatherViewModel

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListPage(
    viewModel: WeatherViewModel,
    initialCity: String,
    onClose: () -> Unit,
) {
    val cityList = remember { mutableStateListOf(initialCity) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf("") }
    var weatherData by remember { mutableStateOf<Map<String, WeatherModel>>(emptyMap()) }
    val weatherResult = viewModel.weatherResult.observeAsState()

    LaunchedEffect(cityList) {
        val newWeatherData = mutableMapOf<String, WeatherModel>()
        for (city in cityList) {
            when (val result = weatherResult.value) {
                is NetworkResponse.Success -> {
                    newWeatherData[city] = result.data
                }
                else -> {}
            }
        }
        weatherData = newWeatherData
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text= "Manage cities") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                cityList.add("New City ${cityList.size + 1}")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add City")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(cityList) { city ->
                val data = weatherData[city]
                if (data != null) {
                    CityCard(
                        city = city,
                        onAction = { action, cityName ->
                            selectedCity = cityName
                            if (action == "delete") {
                                showDialog = true
                            }
                            else{
                                //
                            }
                        },
                        data = data
                    )
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



@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun WeatherDisplay(viewModel: WeatherViewModel, onCountryClick: (String, String) -> Unit) {
    var city by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    val weatherResult = viewModel.weatherResult.observeAsState()

    var isCelsius by remember { mutableStateOf(true) }

    val backgroundColor = when (val result = weatherResult.value) {
        is NetworkResponse.Success -> weatherColors[result.data.current.condition.code]
            ?: Color.LightGray
        is NetworkResponse.Error -> Color.White
        else -> Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = { city = it },
                label = { Text(text = "Enter Location") },
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
        var currentCity by remember { mutableStateOf("") }
        val cityList = remember { mutableStateListOf<String>() }

        if (showCityList) {
            CityListPage(
                viewModel = viewModel,
                country = selectedCountry!!,
                cityList = cityList
            ) { showCityList = false }
        } else {
            when (val result = weatherResult.value) {
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

                NetworkResponse.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp))
                }

                is NetworkResponse.Success -> {
                    currentCity = result.data.location.name
                    WeatherDetail(data = result.data, isCelsius = isCelsius) { country ->
                        onCountryClick(country, currentCity)
                    }
                    Button(onClick = { isCelsius = !isCelsius }) {
                        Text(if (isCelsius) "Switch to Imperial" else "Switch to Metric")
                    }
                }

                null -> {}
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListPage(
    viewModel: WeatherViewModel,
    country: String,
    cityList: MutableList<String>,
    onClose: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf("") }
    var weatherData by remember { mutableStateOf<Map<String, WeatherModel>>(emptyMap()) }
    val weatherResult = viewModel.weatherResult.observeAsState()
    var showAddCityPage by remember { mutableStateOf(false) }
    var showCityAlreadyAddedMessage by remember { mutableStateOf(false) }

    LaunchedEffect(cityList, country) {
        val newWeatherData = mutableMapOf<String, WeatherModel>()
        for (city in cityList) {
            viewModel.fetchWeather(city)
            when (val result = weatherResult.value) {
                is NetworkResponse.Success -> {
                    newWeatherData[city] = result.data
                }

                else -> {}
            }
        }
        weatherData = newWeatherData
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage cities") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCityPage = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add City")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(cityList) { city ->
                val data = weatherData[city]
                if (data != null) {
                    CityCard(
                        city = city,
                        onAction= { action, cityName ->
                            selectedCity = cityName
                            if (action == "delete") {
                                showDialog = true
                            } else {
                                // Handle other actions if needed
                            } },
                            data = data
                    )
                }}
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete $selectedCity?") },
                confirmButton = {Button(onClick = {
                    cityList.remove(selectedCity)
                    showDialog = false
                }) {
                    Text("Delete")
                } },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        if (showAddCityPage) {
            AddCityPage(
                onClose = { showAddCityPage = false },
                viewModel = viewModel,
                initialCountry = country,
                onAddCity = { newCity ->
                    if (cityList.contains(newCity)) {
                        showCityAlreadyAddedMessage = true
                    } else {
                        cityList.add(newCity)
                    }
                },
                cityList = cityList
            )
        }
        if (showCityAlreadyAddedMessage) {
            Snackbar(
                action = {
                    TextButton(onClick = { showCityAlreadyAddedMessage = false }) {
                        Text("OK")
                    }
                         },
                modifier = Modifier.padding(8.dp)
            ) { Text("City is already added to the list.") }
        }
    }
}

@Composable
fun CityCard(city: String, onAction: (String, String) -> Unit, data: WeatherModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier =Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = city, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${data.current.temp_c}°C",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCityPage(
    onClose: () -> Unit,
    onAddCity: (String) -> Unit,
    viewModel: WeatherViewModel,
    initialCountry: String,
    cityList: List<String>
) {
    val topCities = listOf(
        "New York", "Paris", "London", "Tokyo", "Rome",
        "Dubai", "Moscow", "Sydney", "Singapore", "Beijing", "Seoul"
    )

    var topCitiesInCountry by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        topCitiesInCountry = viewModel.fetchTopCitiesForCountry(initialCountry)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add City") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var cityName by remember { mutableStateOf("") }
            OutlinedTextField(
                value = cityName,
                onValueChange = { cityName = it },
                label = { Text("City Name") },
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = {
                    onAddCity(cityName)
                    onClose()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Add City")
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        "Top Cities in $initialCountry",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                }
                items(topCitiesInCountry) { city ->
                    RoundedCityCard(
                        city = city,
                        onAddCity = onAddCity,
                        onClose = onClose,
                        isCityAlreadyAdded = cityList.contains(city)
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item(span = { GridItemSpan(2) }) {
                    Text(
                        "Top Cities",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                }
                items(topCities) { city ->
                    RoundedCityCard(
                        city = city,
                        onAddCity = onAddCity,
                        onClose = onClose,
                        isCityAlreadyAdded = cityList.contains(city)
                    )
                }}
        }
    }
}

@Composable
fun RoundedCityCard(
    city: String,
    onAddCity: (String) -> Unit,
    onClose: () -> Unit,
    isCityAlreadyAdded: Boolean
) {
    val cardColor = if (isCityAlreadyAdded) Color.LightGray else Color.White
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if (!isCityAlreadyAdded) {
                    Modifier.clickable {
                        onAddCity(city)
                        onClose()
                    }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Text(
            text = city,
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp
        )
    }
}




@Composable
fun WeatherDetail(data: WeatherModel, isCelsius: Boolean, onCountryClick: (String) -> Unit) {
    val backgroundColor = weatherColors[data.current.condition.code] ?: Color.LightGray

    val textColor = if (backgroundColor.luminance() > 0.5) Color.Black else Color.White

    var showWindDetail by remember {
        mutableStateOf(false)
    }
    var showHumidityDetail by remember { mutableStateOf(false) }
    var showUVDetail by remember { mutableStateOf(false) }
    var showVisibilityDetail by remember {
        mutableStateOf(false)
    }

    if (showWindDetail) {
        WindDetailPage(
            windDegree = data.current.wind_degree,
            windDir = data.current.wind_dir,
            windchillC = data.current.windchill_c,
            windchillF = data.current.windchill_f,
            gustKph = data.current.gust_kph,
            onBack = { showWindDetail = false }
        )
    } else if (showHumidityDetail) { // Add new condition
        HumidityDetailPage(
            dewpointC = data.current.dewpoString_c,
            dewpointF = data.current.dewpoString_f,
            onBack = { showHumidityDetail = false }
        )
    } else if(showUVDetail){
        UVDetailPage(
            uv = data.current.uv,
            onBack = { showUVDetail = false }
        )
    }else if(showVisibilityDetail) {
        VisibilityDetailPage(
            visibilityKm = data.current.vis_km,
            visibilityMi = data.current.vis_miles,
            onBack = { showVisibilityDetail = false }
        )
    }
    else{
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
                    when (detail.first) {
                        "Wind speed" -> {
                            Card(
                                modifier = Modifier
                                    .clickable { showWindDetail = true }
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                WeatherDetailItem(
                                    label = detail.first,
                                    value = detail.second,
                                    textColor = textColor
                                )
                            }
                        }

                        "Humidity" -> {
                            Card(
                                modifier = Modifier
                                    .clickable { showHumidityDetail = true }
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                WeatherDetailItem(
                                    label = detail.first,
                                    value = detail.second,
                                    textColor = textColor
                                )
                            }
                        }

                        "UV" -> {
                            Card(
                                modifier = Modifier
                                    .clickable { showUVDetail = true }
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                WeatherDetailItem(
                                    label = detail.first,
                                    value = detail.second,
                                    textColor = textColor
                                )
                            }
                        }

                        "Visibility" -> {
                            Card(
                                modifier = Modifier
                                    .clickable { showVisibilityDetail = true }
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                WeatherDetailItem(
                                    label = detail.first,
                                    value = detail.second,
                                    textColor = textColor
                                )
                            }
                        }

                        else -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                WeatherDetailItem(
                                    label = detail.first,
                                    value = detail.second,
                                    textColor = textColor
                                )
                            }
                        }
                    }

                }
            }
        }
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


