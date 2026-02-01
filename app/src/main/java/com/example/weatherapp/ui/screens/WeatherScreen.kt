package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.repo.CachedBundle
import com.example.weatherapp.ui.vm.WeatherViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    vm: WeatherViewModel,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
                actions = { TextButton(onClick = onOpenSettings) { Text("Settings") } }
            )
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (state.loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(10.dp))
            }

            state.bundle?.let { bundle ->
                if (state.isOffline) {
                    AssistChip(onClick = {}, label = { Text("OFFLINE (cached)") })
                    Spacer(Modifier.height(10.dp))
                }
                CurrentBlock(bundle)
                Spacer(Modifier.height(16.dp))
                ForecastBlock(bundle)
            }
        }
    }
}

@Composable
private fun CurrentBlock(b: CachedBundle) {
    val cur = b.forecast.current
    val unitsT = b.forecast.currentUnits?.temperature ?: "°"
    val unitsW = b.forecast.currentUnits?.windSpeed ?: ""

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(b.city.name, style = MaterialTheme.typography.titleLarge)

            val place = listOfNotNull(b.city.admin1, b.city.country).joinToString(", ")
            if (place.isNotBlank()) Text(place)

            val temp = cur?.temperature?.roundToInt()?.toString() ?: "-"
            val feels = cur?.feelsLike?.roundToInt()?.toString() ?: "-"
            val hum = cur?.humidity?.toString() ?: "-"
            val wind = cur?.windSpeed?.roundToInt()?.toString() ?: "-"
            val code = cur?.weatherCode

            Text("Temperature: $temp $unitsT (feels like $feels $unitsT)")
            Text("Condition: ${weatherText(code)} (code=$code)")
            Text("Humidity: $hum %")
            Text("Wind: $wind $unitsW")
            Text("Last update: ${b.updatedIso}")
        }
    }
}

@Composable
private fun ForecastBlock(b: CachedBundle) {
    val d = b.forecast.daily
    val times = d?.time.orEmpty()
    val max = d?.tempMax.orEmpty()
    val min = d?.tempMin.orEmpty()
    val codes = d?.weatherCode.orEmpty()

    val unitMax = b.forecast.dailyUnits?.tempMax ?: "°"
    val unitMin = b.forecast.dailyUnits?.tempMin ?: "°"

    Text("3-day forecast", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(times) { i, day ->
            val tmax = max.getOrNull(i)?.roundToInt()?.toString() ?: "-"
            val tmin = min.getOrNull(i)?.roundToInt()?.toString() ?: "-"
            val code = codes.getOrNull(i)

            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(day, style = MaterialTheme.typography.titleSmall)
                        Text(weatherText(code), style = MaterialTheme.typography.bodyMedium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Max: $tmax $unitMax")
                        Text("Min: $tmin $unitMin")
                    }
                }
            }
        }
    }
}

private fun weatherText(code: Int?): String = when (code) {
    0 -> "Clear"
    1, 2, 3 -> "Partly cloudy"
    45, 48 -> "Fog"
    51, 53, 55 -> "Drizzle"
    61, 63, 65 -> "Rain"
    71, 73, 75 -> "Snow"
    80, 81, 82 -> "Rain showers"
    95 -> "Thunderstorm"
    96, 99 -> "Thunderstorm + hail"
    else -> "Unknown"
}
