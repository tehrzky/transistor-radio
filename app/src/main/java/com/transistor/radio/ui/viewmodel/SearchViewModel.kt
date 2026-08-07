package com.transistor.radio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transistor.radio.data.repository.StationRepository
import com.transistor.radio.domain.model.RadioBrowserStation
import com.transistor.radio.domain.model.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: StationRepository
) : ViewModel() {

    sealed class SearchUiState {
        data object Idle : SearchUiState()
        data object Loading : SearchUiState()
        data class Success(val stations: List<RadioBrowserStation>) : SearchUiState()
        data class Error(val message: String) : SearchUiState()
    }

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState

    private val _directUrl = MutableStateFlow("")
    val directUrl: StateFlow<String> = _directUrl

    fun onDirectUrlChange(url: String) {
        _directUrl.value = url
    }

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            repository.searchRadioBrowser(query)
                .onSuccess { _uiState.value = SearchUiState.Success(it) }
                .onFailure { _uiState.value = SearchUiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun addStationFromBrowser(station: RadioBrowserStation) {
        viewModelScope.launch {
            repository.addStation(station.toStation())
        }
    }

    fun addDirectUrl(url: String, name: String = "Custom Station") {
        if (url.isBlank()) return
        viewModelScope.launch {
            val station = Station(
                id = url.hashCode().toString(),
                name = name,
                streamUrl = url,
                iconUrl = null
            )
            repository.addStation(station)
        }
    }
}
