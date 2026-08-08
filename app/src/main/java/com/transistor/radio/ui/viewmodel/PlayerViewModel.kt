package com.transistor.radio.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.transistor.radio.data.repository.StationRepository
import com.transistor.radio.domain.model.PlaybackState
import com.transistor.radio.domain.model.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application,
    private val repository: StationRepository
) : AndroidViewModel(application) {

    val currentStation: StateFlow<Station?> = repository.currentStation
    val playbackState: StateFlow<PlaybackState> = repository.playbackState

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application)
        .build()
        .apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING ->
                            repository.setPlaybackState(PlaybackState.BUFFERING)
                        Player.STATE_READY ->
                            if (isPlaying) repository.setPlaybackState(PlaybackState.PLAYING)
                        Player.STATE_ENDED ->
                            repository.setPlaybackState(PlaybackState.STOPPED)
                        Player.STATE_IDLE ->
                            repository.setPlaybackState(PlaybackState.STOPPED)
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        repository.setPlaybackState(PlaybackState.PLAYING)
                    } else if (playbackState != Player.STATE_BUFFERING) {
                        repository.setPlaybackState(PlaybackState.PAUSED)
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    repository.setPlaybackState(PlaybackState.ERROR)
                }
            })
        }

    fun playStation(station: Station) {
        viewModelScope.launch {
            repository.setCurrentStation(station)
            exoPlayer.setMediaItem(MediaItem.fromUri(station.streamUrl))
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.currentMediaItem == null) {
                currentStation.value?.let { playStation(it) }
            } else {
                exoPlayer.play()
            }
        }
    }

    fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        repository.setPlaybackState(PlaybackState.STOPPED)
    }

    override fun onCleared() {
        exoPlayer.release()
        super.onCleared()
    }
}
