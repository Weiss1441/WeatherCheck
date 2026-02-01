package com.example.weatherapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.remote.GeoCity
import com.example.weatherapp.ui.vm.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    vm: SearchViewModel,
    onOpenSettings: () -> Unit,
    onCitySelected: (GeoCity) -> Unit
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search City") },
                actions = { TextButton(onClick = onOpenSettings) { Text("Settings") } }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::setQuery,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("City name") },
                singleLine = true
            )

            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = vm::search,
                    enabled = !state.loading,
                    modifier = Modifier.weight(1f)
                ) { Text("Search") }

                Spacer(Modifier.width(10.dp))

                if (state.loading) {
                    CircularProgressIndicator(modifier = Modifier)
                }
            }

            state.error?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.results) { city ->
                    CityRow(city = city, onClick = { onCitySelected(city) })
                }
            }
        }
    }
}

@Composable
private fun CityRow(city: GeoCity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(city.name, style = MaterialTheme.typography.titleMedium)
            val sub = listOfNotNull(city.admin1, city.country).joinToString(", ")
            if (sub.isNotBlank()) Text(sub, style = MaterialTheme.typography.bodyMedium)
            Text("lat=${city.latitude}, lon=${city.longitude}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
