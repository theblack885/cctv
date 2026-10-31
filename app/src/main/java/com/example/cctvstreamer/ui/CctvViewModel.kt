package com.example.cctvstreamer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.cctvstreamer.data.CctvRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CctvViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CctvRepository(application)
    var exoPlayer: ExoPlayer? = null
    private var currentUrl: String? = null

    private val _status = MutableStateFlow("Camera URL daalo")
    val status: StateFlow<String> = _status.asStateFlow()

    // Same URL par purana player milega, naya URL ho to naya player banega
    fun initializePlayer(url: String): ExoPlayer {
        val cleanUrl = url.trim()
        val existing = exoPlayer
        if (existing != null && currentUrl == cleanUrl) {
            return existing
        }

        existing?.release()
        _status.value = "Connect ho raha hai..."

        val player = repository.createPlayer(cleanUrl)
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> _status.value = "Buffering..."
                    Player.STATE_READY -> _status.value = "Live chal raha hai"
                    Player.STATE_ENDED -> _status.value = "Stream khatam ho gaya"
                    else -> {}
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _status.value = "Error: " + error.errorCodeName
            }
        })

        exoPlayer = player
        currentUrl = cleanUrl
        return player
    }

    // Refresh button ke liye: zabardasti dobara connect karo
    fun reconnect(url: String): ExoPlayer {
        currentUrl = null
        return initializePlayer(url)
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
}
