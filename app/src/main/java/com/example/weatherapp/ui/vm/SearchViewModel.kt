package com.example.weatherapp.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.GeoCity
import com.example.weatherapp.data.repo.CachedBundle
import com.example.weatherapp.data.repo.RepoResult
import com.example.weatherapp.data.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val results: List<GeoCity> = emptyList(),
    val cached: CachedBundle? = null
)

class SearchViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state

    init {
        loadCache()
    }

    fun onQueryChange(text: String) {
        _state.update { it.copy(query = text) }
    }

    fun onSearch() {
        val q = _state.value.query
        _state.update { it.copy(isLoading = true, error = null, results = emptyList()) }

        viewModelScope.launch {
            when (val r = repository.searchCities(q)) {
                is RepoResult.Success -> {
                    _state.update { it.copy(isLoading = false, results = r.data, error = null) }
                }
                is RepoResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = r.message, results = emptyList()) }
                    // если нет интернета — покажем кэш
                    if (r.message.contains("No internet", ignoreCase = true)) {
                        loadCache()
                    }
                }
            }
        }
    }

    fun refreshCache() {
        loadCache()
    }

    private fun loadCache() {
        viewModelScope.launch {
            val cached = repository.getCachedBundle()
            _state.update { it.copy(cached = cached) }
        }
    }
}
