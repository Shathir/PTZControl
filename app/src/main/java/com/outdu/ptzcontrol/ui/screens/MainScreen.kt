package com.outdu.ptzcontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.outdu.ptzcontrol.R
import com.outdu.ptzcontrol.client.PTZClient
import com.outdu.ptzcontrol.services.OnvifDevice
import com.outdu.ptzcontrol.ui.components.CameraStreamLayout
import com.outdu.ptzcontrol.ui.components.CameraStreamLayoutUltraLowLatency
import com.outdu.ptzcontrol.ui.components.CameraStreamLayoutWithControls
import com.outdu.ptzcontrol.ui.components.DPadLayout
import com.outdu.ptzcontrol.ui.components.InfoCard
import com.outdu.ptzcontrol.ui.components.PresetRow
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    selectedDevice: OnvifDevice,
    onBackToDiscovery: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State to trigger preset operations
    var savePresetTrigger by remember { mutableIntStateOf(0) }
    var deletePresetTrigger by remember { mutableIntStateOf(0) }
    var reloadPresetsTrigger by remember { mutableIntStateOf(0) }
    
    // State to track selected preset
    var selectedPresetNumber by remember { mutableStateOf<String?>(null) }
    
    // State to track calibration mode
    var isCalibrationMode by remember { mutableStateOf(false) }
    
    // Coroutine scope and PTZ client for fetching initial mode
    val coroutineScope = rememberCoroutineScope()
    val ptzClient = remember(selectedDevice.ipAddress) { PTZClient(selectedDevice.ipAddress) }
    
    // Fetch initial mode when screen loads
    LaunchedEffect(selectedDevice.ipAddress) {
        coroutineScope.launch {
            try {
                ptzClient.init()
                val currentMode = ptzClient.getCurrentMode()
                currentMode?.let { mode ->
                    isCalibrationMode = (mode == 1)
                }
            } catch (e: Exception) {
                // If we can't fetch the mode, default to runtime (false)
                isCalibrationMode = false
            }
        }
    }

    // Column to fill the complete screen in a vertical stack.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    )
    {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .background(Color.Transparent),
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(Color.Transparent)
                        .clickable {
                            onBackToDiscovery()
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back), // Using reload icon as back arrow placeholder
                        contentDescription = "Back to device selection",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Device info and InfoCard
                Box(modifier = Modifier.weight(1f)) {
                    InfoCard(
                        onReloadPresets = {
                            reloadPresetsTrigger += 1
                        }, 
                        onGoToPreset = {
                            println("Go to preset button pressed - Preset: $selectedPresetNumber")
                            // This will be handled by InfoCard now
                        },
                        selectedPresetNumber = selectedPresetNumber,
                        deviceIpAddress = selectedDevice.ipAddress,
                        isCalibrationMode = isCalibrationMode,
                        onModeChanged = { calibrationMode ->
                            isCalibrationMode = calibrationMode
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Transparent)
        )
        {
            PresetRow(
                savePresetTrigger = savePresetTrigger,
                deletePresetTrigger = deletePresetTrigger,
                reloadPresetsTrigger = reloadPresetsTrigger,
                onSelectedPresetChanged = { presetNumber ->
                    selectedPresetNumber = presetNumber
                },
                deviceIpAddress = selectedDevice.ipAddress,
                isCalibrationMode = isCalibrationMode
            )
        }

        // Top half screen for showing stream
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        )
        {
//            CameraStreamLayout()
//            CameraStreamLayoutWithControls()
            CameraStreamLayoutUltraLowLatency(
                rtspUrl = "rtsp://" + selectedDevice.ipAddress +  ":8004/live1.sdp"
            )
        }

        // Bottom half screen for showing controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        )
        {
            DPadLayout(
                onSavePreset = { 
                    savePresetTrigger += 1 
                },
                onDeletePreset = {
                    deletePresetTrigger += 1
                },
                deviceIpAddress = selectedDevice.ipAddress,
                isCalibrationMode = isCalibrationMode
            )
        }

    }
}