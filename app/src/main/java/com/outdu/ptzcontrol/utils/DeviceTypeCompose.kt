package com.outdu.ptzcontrol.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.outdu.ptzcontrol.utils.DeviceTypeDetector

/**
 * Compose utilities for device type detection
 */

/**
 * Composable function to get device type
 */
@Composable
fun rememberDeviceType(): DeviceTypeDetector.DeviceType {
    val context = LocalContext.current
    return remember {
        DeviceTypeDetector(context).getDeviceType()
    }
}

/**
 * Composable function to check if current device is tablet
 */
@Composable
fun rememberIsTablet(): Boolean {
    val context = LocalContext.current
    return remember {
        DeviceTypeDetector.isTablet(context)
    }
}

/**
 * Composable function to check if current device is phone
 */
@Composable
fun rememberIsPhone(): Boolean {
    val context = LocalContext.current
    return remember {
        DeviceTypeDetector.isPhone(context)
    }
}

/**
 * Composable function to get comprehensive device info
 */
@Composable
fun rememberDeviceInfo(): DeviceTypeDetector.DeviceInfo {
    val context = LocalContext.current
    return remember {
        DeviceTypeDetector(context).getDeviceInfo()
    }
}

/**
 * Composable that conditionally renders content based on device type
 */
@Composable
fun DeviceTypeContent(
    phoneContent: @Composable () -> Unit = {},
    tabletContent: @Composable () -> Unit = {}
) {
    val isTablet = rememberIsTablet()
    
    if (isTablet) {
        tabletContent()
    } else {
        phoneContent()
    }
}
