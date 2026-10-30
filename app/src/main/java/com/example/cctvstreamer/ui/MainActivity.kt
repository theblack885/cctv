package com.example.cctvstreamer.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.ui.PlayerView
import com.example.cctvstreamer.R
import com.example.cctvstreamer.service.CctvStreamService

class MainActivity : AppCompatActivity() {

    private val viewModel: CctvViewModel by viewModels()
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)

        // Yahan apna authorized CCTV ka RTSP link dalein (Jaise: rtsp://admin:password@192.168.1.100:554/stream1)
        val sampleRtspUrl = "rtsp://your_camera_ip_here:554/stream1"

        val player = viewModel.initializePlayer(sampleRtspUrl)
        playerView.player = player

        startCctvService()
    }

    private fun startCctvService() {
        val serviceIntent = Intent(this, CctvStreamService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}
