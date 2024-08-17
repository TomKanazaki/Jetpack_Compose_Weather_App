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
fun VisibilityDetailPage(visibilityKm: String, visibilityMi: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visibility Details") },
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
            Text("Visibility (Kilometers): $visibilityKm km", fontSize = 18.sp)
            Text("Visibility (Miles): $visibilityMi mi", fontSize =18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "About Visibility:\n\n" +
                        "Visibility tells you how far away you can clearly see objects like buildings and hills. " +
                        "It is a measure of the transparency of the air and does not take into account the amount of sunlight or the presence of obstructions. " +
                        "Visibility at or above 10 km is considered clear.",
                fontSize = 16.sp
            )
        }
    }
}