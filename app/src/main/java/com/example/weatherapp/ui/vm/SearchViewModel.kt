package com.example.weatherapp.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.GeoCity
import com.example.weatherapp.data.repo.RepoResult
import com.example.weatherapp.data.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val results: List<GeoCity> = emptyList()
)

class SearchViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state

    fun setQuery(q: String) {
        _state.value = _state.value.copy(query = q, error = null)
    }

    fun search() {
        val q = _state.value.query.trim()
        if (q.isBlank()) {
            _state.value = _state.value.copy(error = "Enter a city name.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null, results = emptyList())
            when (val res = repo.searchCities(q)) {
                is RepoResult.Success -> _state.value = _state.value.copy(
                    loading = false,
                    results = res.data
                )
                is RepoResult.Error -> _state.value = _state.value.copy(
                    loading = false,
                    error = res.message
                )
            }
        }
    }
}
