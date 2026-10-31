package com.example.cctvstreamer.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.PlayerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CctvScreen()
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun CctvScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("cctv", Context.MODE_PRIVATE) }

    var url by remember { mutableStateOf(prefs.getString("url", "") ?: "") }
    var status by remember { mutableStateOf("URL daalo aur Play dabao") }
    var forceTcp by remember { mutableStateOf(true) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> status = "Buffering..."
                        Player.STATE_READY -> status = "Live chal raha hai"
                        Player.STATE_ENDED -> status = "Stream khatam ho gaya"
                        else -> {}
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    status = "Error: " + error.errorCodeName + "\n" + (error.message ?: "")
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    fun startPlay() {
        val link = url.trim()
        if (link.isEmpty()) {
            status = "Pehle camera ka URL daalo"
            return
        }
        prefs.edit().putString("url", link).apply()
        val item = MediaItem.fromUri(link)
        if (link.startsWith("rtsp://", ignoreCase = true)) {
            val source = RtspMediaSource.Factory()
                .setForceUseRtpTcp(forceTcp)
                .createMediaSource(item)
            player.setMediaSource(source)
        } else {
            player.setMediaItem(item)
        }
        player.prepare()
        player.playWhenReady = true
        status = "Connect ho raha hai..."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("CCTV Streamer", style = MaterialTheme.typography.headlineSmall)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                    }
                }
            )
        }

        Text(status, style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Camera URL (rtsp://user:pass@192.168.1.64:554/stream1)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = forceTcp, onCheckedChange = { forceTcp = it })
            Spacer(modifier = Modifier.height(0.dp).padding(start = 8.dp))
            Text("TCP use karo (zyada tar cameras ke liye behtar)")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { startPlay() }) { Text("Play") }
            OutlinedButton(onClick = {
                player.stop()
                status = "Band kiya"
            }) { Text("Stop") }
        }
    }
}
