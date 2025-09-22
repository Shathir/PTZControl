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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

@Composable
fun InfoCard(
    onReloadPresets: (() -> Unit)? = null,
    onGoToPreset: (() -> Unit)? = null,
    selectedPresetNumber: String? = null,
    deviceIpAddress: String? = null
) {
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
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight(500)
            )
        )

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            Box(
                modifier = Modifier
                    .size(40.dp)
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
                    modifier = Modifier.size(36.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .clickable {
                        println("Play Button Pressed - Going to preset: $selectedPresetNumber")
                        selectedPresetNumber?.let { presetNumber ->
                            coroutineScope.launch {
                                try {
                                    ptzClient.init()
                                    val (success, message) = ptzClient.goToPreset(presetNumber)
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
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}