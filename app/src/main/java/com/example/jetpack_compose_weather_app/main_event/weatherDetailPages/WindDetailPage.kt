import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WindDetailPage(
    windDegree: String,
    windDir: String,
    windchillC: String,
    windchillF: String,
    gustKph: String,
    onBack: () -> Unit // Add onBack lambda
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wind Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            Text("Gust speed: $gustKph", fontSize = 18.sp)
            Text("Wind Direction (degrees): $windDegree", fontSize = 18.sp)
            Text("Wind Direction (compass): $windDir", fontSize = 18.sp)
            Text("Windchill (Celsius): $windchillC °C", fontSize = 18.sp)
            Text("Windchill (Fahrenheit): $windchillF °F", fontSize = 18.sp)
            Text(
                "\nAbout Wind Speed and Gusts:\n\n" +
                        "The wind speed is calculated using the average over a short period of time. " +
                        "Gusts are short bursts of wind above this average. A gust typically lasts under 20 seconds.",
                fontSize = 16.sp
            )
        }
    }
}