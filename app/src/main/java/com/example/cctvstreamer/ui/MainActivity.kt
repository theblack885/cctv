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
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.ui.PlayerView
import com.example.cctvstreamer.service.CctvStreamService

class MainActivity : ComponentActivity() {

    private val viewModel: CctvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Secured gateway or authorized stream endpoint
        val secureStreamUrl = "rtsp://your_authorized_gateway_ip:554/stream"
        val player = viewModel.initializePlayer(secureStreamUrl)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0B0F19)
            ) {
                SecureCctvScreen(player = player, onReconnect = {
                    player.prepare()
                    player.play()
                })
            }
        }

        startBackgroundService()
    }

    private fun startBackgroundService() {
        val serviceIntent = Intent(this, CctvStreamService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureCctvScreen(player: androidx.media3.exoplayer.ExoPlayer, onReconnect: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURE // FEED MONITOR", 
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF131B2E)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0B0F19))
        ) {
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

                // Live Secure Indicator
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
                            text = "SECURE LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                // Reconnect / Refresh Control
                FloatingActionButton(
                    onClick = onReconnect,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color.Black
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync Stream")
                }
            }
        }
    }
}
