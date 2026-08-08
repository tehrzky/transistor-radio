package com.transistor.radio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transistor.radio.data.repository.StationRepository
import com.transistor.radio.domain.model.PlaybackState
import com.transistor.radio.domain.model.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: StationRepository
) : ViewModel() {

    val currentStation: StateFlow<Station?> = repository.currentStation
    val playbackState: StateFlow<PlaybackState> = repository.playbackState

    fun playStation(station: Station) {
        viewModelScope.launch {
            repository.setCurrentStation(station)
            repository.setPlaybackState(PlaybackState.BUFFERING)
            delay(800)
            repository.setPlaybackState(PlaybackState.PLAYING)
        }
    }

    fun togglePlayPause() {
        val current = playbackState.value
        if (current == PlaybackState.PLAYING) {
            repository.setPlaybackState(PlaybackState.PAUSED)
        } else {
            repository.setPlaybackState(PlaybackState.PLAYING)
        }
    }

    fun stop() {
        repository.setPlaybackState(PlaybackState.STOPPED)
    }
}
