package com.outdu.ptzcontrol.ui.components

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.LoadControl
import androidx.media3.ui.PlayerView


/*
RTSP Stream Player using ExoPlayer
*/
@OptIn(UnstableApi::class)
@Composable
fun CameraStreamLayout(
    rtspUrl: String = "rtsp://192.168.1.162:8554/live/pano_stream_enc2" // Default RTSP URL
) {
    val TAG = "RTSP Stream"
    val context = LocalContext.current
    
    // Create low-latency LoadControl for minimal buffering
    val lowLatencyLoadControl = remember {
        DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                50,    // Min buffer duration (50ms)
                200,   // Max buffer duration (200ms) 
                50,    // Buffer for playback (50ms)
                50     // Buffer for playback after rebuffer (50ms)
            )
            .setTargetBufferBytes(-1) // Use default
            .setPrioritizeTimeOverSizeThresholds(true) // Prioritize low latency
            .build()
    }

    // Create ExoPlayer instance with low-latency configuration
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(lowLatencyLoadControl)
            .build().apply {
                // Add listener for playback events
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_IDLE -> Log.d(TAG, "Player idle")
                            Player.STATE_BUFFERING -> Log.d(TAG, "Player buffering")
                            Player.STATE_READY -> Log.i(TAG, "Player ready - low latency stream started")
                            Player.STATE_ENDED -> Log.d(TAG, "Player ended")
                        }
                    }
                    
                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        Log.d(TAG, "Video size: ${videoSize.width}x${videoSize.height}")
                    }
                    
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(TAG, "Player error: ${error.message}", error)
                    }
                })
            }
    }

    // Set up the media item and start playback
    LaunchedEffect(rtspUrl) {
        try {
            Log.i(TAG, "Setting up RTSP stream: $rtspUrl")
            val mediaItem = MediaItem.fromUri(rtspUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RTSP stream", e)
        }
    }

    // Clean up when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            Log.i(TAG, "Disposing ExoPlayer")
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(48.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // Hide player controls
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/*
Alternative version with stream controls (play/pause/reload)
Usage: Replace CameraStreamLayout() with CameraStreamLayoutWithControls() in MainScreen.kt
*/
@OptIn(UnstableApi::class)
@Composable
fun CameraStreamLayoutWithControls(
    rtspUrl: String = "rtsp://192.168.1.162:8554/live/pano_stream_enc2"
) {
    val TAG = "RTSP Stream"
    val context = LocalContext.current
    
    // Create low-latency LoadControl for minimal buffering
    val lowLatencyLoadControl = remember {
        DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                50,    // Min buffer duration (50ms)
                200,   // Max buffer duration (200ms) 
                50,    // Buffer for playback (50ms)
                50     // Buffer for playback after rebuffer (50ms)
            )
            .setTargetBufferBytes(-1) // Use default
            .setPrioritizeTimeOverSizeThresholds(true) // Prioritize low latency
            .build()
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(lowLatencyLoadControl)
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_IDLE -> Log.d(TAG, "Player idle")
                            Player.STATE_BUFFERING -> Log.d(TAG, "Player buffering")
                            Player.STATE_READY -> Log.i(TAG, "Player ready - low latency stream with controls started")
                            Player.STATE_ENDED -> Log.d(TAG, "Player ended")
                        }
                    }
                    
                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        Log.d(TAG, "Video size: ${videoSize.width}x${videoSize.height}")
                    }
                    
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(TAG, "Player error: ${error.message}", error)
                    }
                })
            }
    }

    LaunchedEffect(rtspUrl) {
        try {
            Log.i(TAG, "Setting up RTSP stream with controls: $rtspUrl")
            val mediaItem = MediaItem.fromUri(rtspUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RTSP stream", e)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.i(TAG, "Disposing ExoPlayer with controls")
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(48.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true // Show player controls
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/*
Ultra Low-Latency version (Experimental)
Usage: Replace CameraStreamLayout() with CameraStreamLayoutUltraLowLatency() for minimal latency
Note: May cause more frequent buffering but provides lowest possible latency
*/
@OptIn(UnstableApi::class)
@Composable
fun CameraStreamLayoutUltraLowLatency(
    rtspUrl: String = "rtsp://192.168.1.162:8004/live1.sdp"
) {
    val TAG = "RTSP Stream Ultra"
    val context = LocalContext.current
    
    // Ultra low-latency LoadControl (aggressive settings)
    val ultraLowLatencyLoadControl = remember {
        DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                25,    // Min buffer duration (25ms - very aggressive)
                100,   // Max buffer duration (100ms - minimal buffering)
                25,    // Buffer for playback (25ms)
                25     // Buffer for playback after rebuffer (25ms)
            )
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true)
            .setBackBuffer(0, false) // Disable back buffer for lowest latency
            .build()
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(ultraLowLatencyLoadControl)
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_IDLE -> Log.d(TAG, "Ultra low-latency player idle")
                            Player.STATE_BUFFERING -> Log.d(TAG, "Ultra low-latency player buffering")
                            Player.STATE_READY -> Log.i(TAG, "Ultra low-latency stream ready")
                            Player.STATE_ENDED -> Log.d(TAG, "Ultra low-latency player ended")
                        }
                    }
                    
                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        Log.d(TAG, "Ultra low-latency video size: ${videoSize.width}x${videoSize.height}")
                    }
                    
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(TAG, "Ultra low-latency player error: ${error.message}", error)
                    }
                })
            }
    }

    LaunchedEffect(rtspUrl) {
        try {
            Log.i(TAG, "Setting up ultra low-latency RTSP stream: $rtspUrl")
            val mediaItem = MediaItem.fromUri(rtspUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ultra low-latency RTSP stream", e)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.i(TAG, "Disposing ultra low-latency ExoPlayer")
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(48.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}