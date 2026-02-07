package com.example.weatherapp.ui.vm

import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.firebase.FavoritesRepository
import com.example.weatherapp.data.model.FavoriteCity
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesViewModel(
    private val repo: FavoritesRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteCity>>(emptyList())
    val favorites: StateFlow<List<FavoriteCity>> = _favorites.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var listener: ValueEventListener? = null

    fun start() {
        if (listener != null) return
        listener = repo.listen(
            onUpdate = { _favorites.value = it },
            onError = { _error.value = it }
        )
    }

    fun stop() {
        listener?.let { repo.stop(it) }
        listener = null
    }

    fun add(cityName: String, note: String = "") {
        repo.add(cityName, note) { _error.value = it }
    }

    fun updateNote(id: String, note: String) {
        repo.updateNote(id, note) { _error.value = it }
    }

    fun delete(id: String) {
        repo.delete(id) { _error.value = it }
    }

    override fun onCleared() {
        stop()
        super.onCleared()
    }
}
