package com.example.cctvstreamer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.example.cctvstreamer.data.CctvRepository

class CctvViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CctvRepository(application)
    var exoPlayer: ExoPlayer? = null

    fun initializePlayer(url: String): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = repository.createPlayer(url)
        }
        return exoPlayer!!
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
}
