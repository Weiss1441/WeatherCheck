package com.example.weatherapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.remote.GeoCity
import com.example.weatherapp.ui.vm.SearchViewModel

@Composable
fun SearchScreen(
    vm: SearchViewModel,
    onOpenSettings: () -> Unit,
    onCitySelected: (GeoCity) -> Unit
) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Search City", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onOpenSettings) { Text("Settings") }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.query,
            onValueChange = { vm.onQueryChange(it) },
            label = { Text("City name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { vm.onSearch() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Spacer(Modifier.height(12.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
        }

        // ✅ Offline cache block: показываем кнопку "Open cached weather"
        val showOfflineCache =
            (state.error?.contains("No internet", ignoreCase = true) == true) && state.cached != null

        if (showOfflineCache) {
            Text("OFFLINE (cached)", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))

            Card {
                Column(Modifier.padding(16.dp)) {
                    Text(state.cached!!.city.name, style = MaterialTheme.typography.titleMedium)

                    val subtitle = listOfNotNull(
                        state.cached!!.city.admin1,
                        state.cached!!.city.country
                    ).joinToString(", ")

                    if (subtitle.isNotBlank()) Text(subtitle)

                    Spacer(Modifier.height(6.dp))
                    Text("Last update: ${state.cached!!.updatedIso}")
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onCitySelected(state.cached!!.city) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open cached weather")
            }

            Spacer(Modifier.height(16.dp))
        }

        // Results list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)   //важно
        ) {
            items(state.results) { city ->
                CityRow(city = city, onClick = { onCitySelected(city) })
                Spacer(Modifier.height(10.dp))
            }
        }

    }
}

@Composable
private fun CityRow(city: GeoCity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(city.name, style = MaterialTheme.typography.titleMedium)

            val subtitle = listOfNotNull(city.admin1, city.country).joinToString(", ")
            if (subtitle.isNotBlank()) Text(subtitle)

            Spacer(Modifier.height(4.dp))
            Text("lat=${city.latitude}, lon=${city.longitude}")
        }
    }
}
