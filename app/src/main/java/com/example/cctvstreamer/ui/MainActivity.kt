package com.example.cctvstreamer.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.ui.PlayerView
import com.example.cctvstreamer.service.CctvStreamService
import com.example.cctvstreamer.ui.theme.CCTVStreamerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CctvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sampleRtspUrl = "rtsp://your_camera_ip_here:554/stream1"
        val player = viewModel.initializePlayer(sampleRtspUrl)

        setContent {
            CCTVStreamerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CCTVHomeScreen(player = player, onReconnect = {
                        // Reconnect logic agar stream pause/fail ho jaye
                        player.prepare()
                        player.play()
                    })
                }
            }
        }

        startCctvService()
    }

    private fun startCctvService() {
        val serviceIntent = Intent(this, CctvStreamService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CCTVHomeScreen(player: androidx.media3.exoplayer.ExoPlayer, onReconnect: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KAIZO // SECURE-FEED", 
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Video Player Container with Modern Rounded Corners & Border Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            this.player = player
                            useController = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Live Indicator Badge (Top Right)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(50),
                    color = Color.Red.copy(alpha = 0.8f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                // Floating Reconnect Button (Bottom Center)
                FloatingActionButton(
                    onClick = onReconnect,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reconnect")
                }
            }
        }
    }
}
