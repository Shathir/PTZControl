package com.outdu.ptzcontrol.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.outdu.ptzcontrol.R
import com.outdu.ptzcontrol.client.PTZClient
import kotlinx.coroutines.launch
import com.outdu.ptzcontrol.utils.rememberDeviceType
import com.outdu.ptzcontrol.utils.DeviceType

@Composable
fun InfoCard(
    onReloadPresets: (() -> Unit)? = null,
    onGoToPreset: (() -> Unit)? = null,
    selectedPresetNumber: String? = null,
    deviceIpAddress: String? = null,
    isCalibrationMode: Boolean = false, // Receive mode state from parent
    onModeChanged: ((Boolean) -> Unit)? = null // Callback to notify parent about mode changes
) {
    val deviceType = rememberDeviceType()
    
    // Mobile-optimized sizing
    val titleFontSize = if (deviceType == DeviceType.TABLET) 24.sp else 18.sp
    val modeFontSize = if (deviceType == DeviceType.TABLET) 14.sp else 12.sp
    val buttonSize = if (deviceType == DeviceType.TABLET) 40.dp else 30.dp
    val iconSize = if (deviceType == DeviceType.TABLET) 36.dp else 27.dp
    val playIconSize = if (deviceType == DeviceType.TABLET) 16.dp else 12.dp
    val spacing = if (deviceType == DeviceType.TABLET) 12.dp else 9.dp
    val modeSpacing = if (deviceType == DeviceType.TABLET) 8.dp else 6.dp
    val TAG = "InfoCard"
    val coroutineScope = rememberCoroutineScope()
    val ptzClient = remember(deviceIpAddress) { PTZClient(deviceIpAddress) }
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        Text(
            text = "PTZ Control",
            style = TextStyle(
                color = Color(0xFF2A2A2A),
                fontSize = titleFontSize,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight(500)
            )
        )

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing)
        )
        {
            // Mode toggle section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(modeSpacing)
            ) {
                Text(
                    text = if (isCalibrationMode) "Calibration" else "Runtime",
                    style = TextStyle(
                        color = if (isCalibrationMode) Color(0xFF4CAF50) else Color(0xFFFF6B35),
                        fontSize = modeFontSize,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight(500)
                    )
                )
                
                Switch(
                    checked = isCalibrationMode,
                    onCheckedChange = { newMode ->
                        val mode = if (newMode) 1 else 0
                        val modeText = if (newMode) "Calibration" else "Runtime"
                        
                        coroutineScope.launch {
                            try {
                                ptzClient.init()
                                val success = ptzClient.changeMode(mode)
                                if (success) {
                                    Log.i(TAG, "Successfully changed to $modeText mode")
                                    // Notify parent about mode change only if API call succeeds
                                    onModeChanged?.invoke(newMode)
                                } else {
                                    Log.w(TAG, "Failed to change to $modeText mode")
                                    // Don't change state if API call failed
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error changing to $modeText mode", e)
                                // Don't change state if there was an error
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFFF6B35)
                    )
                )
            }
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .clickable {
                        println("Reload Button Pressed")
                        onReloadPresets?.invoke()
                    },
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    painter = painterResource(id = R.drawable.reload),
                    contentDescription = "Reload Button",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
                )
            }

            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .clickable {
                        println("Play Button Pressed - Going to preset: $selectedPresetNumber")
                        selectedPresetNumber?.let { presetNumber ->
                            coroutineScope.launch {
                                try {
                                    ptzClient.init()
                                    val currentMode = if (isCalibrationMode) 1 else 0
                                    val (success, message) = ptzClient.goToPreset(presetNumber, currentMode)
                                    if (success) {
                                        Log.i(TAG, "Successfully went to preset $presetNumber")
                                    } else {
                                        Log.w(TAG, "Failed to go to preset $presetNumber: $message")
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error going to preset $presetNumber", e)
                                }
                            }
                        } ?: run {
                            Log.w(TAG, "No preset selected")
                        }
                        onGoToPreset?.invoke()
                    },
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = "Play Button",
                    tint = Color.White,
                    modifier = Modifier.size(playIconSize)
                )
            }
        }
    }
}