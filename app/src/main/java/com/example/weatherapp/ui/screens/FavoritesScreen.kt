package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.model.FavoriteCity
import com.example.weatherapp.ui.vm.FavoritesViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FavoritesScreen(
    vm: FavoritesViewModel,
    onBack: () -> Unit
) {
    val list by vm.favorites.collectAsState()
    val error by vm.error.collectAsState()

    var showAdd by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<FavoriteCity?>(null) }

    LaunchedEffect(Unit) { vm.start() }
    DisposableEffect(Unit) { onDispose { vm.stop() } }

    if (showAdd) {
        AddDialog(
            onDismiss = { showAdd = false },
            onAdd = { city, note ->
                vm.add(city, note)
                showAdd = false
            }
        )
    }

    editItem?.let { item ->
        EditNoteDialog(
            initial = item.note,
            onDismiss = { editItem = null },
            onSave = { note ->
                vm.updateNote(item.id, note)
                editItem = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            if (error != null) {
                Text(text = "Error: $error")
                Spacer(Modifier.height(8.dp))
            }

            if (list.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favorites yet")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(list, key = { it.id }) { item ->
                        FavoriteRow(
                            item = item,
                            onEdit = { editItem = item },
                            onDelete = { vm.delete(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteRow(
    item: FavoriteCity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Column(Modifier.padding(12.dp)) {
            Text(text = item.cityName, style = MaterialTheme.typography.titleMedium)
            if (item.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(text = item.note, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Edit note") }
                OutlinedButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}

@Composable
private fun AddDialog(
    onDismiss: () -> Unit,
    onAdd: (city: String, note: String) -> Unit
) {
    var city by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add favorite") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(city, note) },
                enabled = city.trim().isNotEmpty()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun EditNoteDialog(
    initial: String,
    onDismiss: () -> Unit,
    onSave: (note: String) -> Unit
) {
    var note by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit note") },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(note) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
