package com.outdu.ptzcontrol

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.outdu.ptzcontrol.services.OnvifDevice
import com.outdu.ptzcontrol.ui.screens.DeviceDiscoveryScreen
import com.outdu.ptzcontrol.ui.screens.MainScreen
import com.outdu.ptzcontrol.ui.theme.PTZControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.let {
                // Hide both the status bar and the navigation bar
                it.hide(WindowInsets.Type.systemBars())

                // Allow user swipe to bring them back temporarily
                it.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            PTZControlTheme {
                var selectedDevice by remember { mutableStateOf<OnvifDevice?>(null) }
                
                if (selectedDevice == null) {
                    DeviceDiscoveryScreen(
                        onDeviceSelected = { device ->
                            selectedDevice = device
                        }
                    )
                } else {
                    MainScreen(
                        selectedDevice = selectedDevice!!,
                        onBackToDiscovery = {
                            selectedDevice = null
                        }
                    )
                }
            }
        }
    }
}