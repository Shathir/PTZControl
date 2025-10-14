package com.outdu.ptzcontrol.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

enum class DeviceType {PHONE, TABLET, FOLDABLE}

@Composable
fun rememberDeviceType(): DeviceType {
    val configuration = LocalConfiguration.current
    val shortestWidthDp = minOf(configuration.screenWidthDp, configuration.screenHeightDp)

    Log.d("Density Calculator", "Shortest width: $shortestWidthDp")
    return when {
        shortestWidthDp < 600 -> DeviceType.PHONE
        else -> DeviceType.TABLET
    }
}
