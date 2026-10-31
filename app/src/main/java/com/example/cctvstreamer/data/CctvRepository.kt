package com.example.cctvstreamer.data

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource

class CctvRepository(private val context: Context) {

    @OptIn(UnstableApi::class)
    fun createPlayer(streamUrl: String): ExoPlayer {
        val url = streamUrl.trim()
        return ExoPlayer.Builder(context).build().apply {
            val item = MediaItem.fromUri(url)

            if (url.startsWith("rtsp://", ignoreCase = true)) {
                val mediaSource = RtspMediaSource.Factory()
                    .setForceUseRtpTcp(true)
                    .createMediaSource(item)
                setMediaSource(mediaSource)
            } else {
                // http / https (mp4, mjpeg etc.)
                setMediaItem(item)
            }

            prepare()
            playWhenReady = true
        }
    }
}
