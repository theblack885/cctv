package com.example.cctvstreamer.data

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource

class CctvRepository(private val context: Context) {

    fun createPlayer(rtspUrl: String): ExoPlayer {
        return ExoPlayer.Builder(context).build().apply {
            val mediaSource = RtspMediaSource.Factory()
                .setForceUseRtpTcp(true)
                .createMediaSource(MediaItem.fromUri(rtspUrl))
            
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
    }
}
