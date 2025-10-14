package com.outdu.ptzcontrol.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.outdu.ptzcontrol.client.PTZClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.outdu.ptzcontrol.utils.rememberDeviceType
import com.outdu.ptzcontrol.utils.DeviceType

@Composable
fun DPadController(
    size: Dp = 250.dp,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    isCalibrationMode: Boolean = false
) {
    val deviceType = rememberDeviceType()
    val scaledSize = if (deviceType == DeviceType.TABLET) size else size * 0.75f
    val sectorColor = Color(0xFFE8E8E8)
    val borderColor = Color(0xFFD0D0D0)

    Box(
        modifier = Modifier.size(scaledSize),
        contentAlignment = Alignment.Center
    ) {
        // Draw the circular D-Pad with arc sectors
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isCalibrationMode) {
                    // Handle touch events only if in calibration mode
                    if (isCalibrationMode) {
                        detectTapGestures { offset ->
                            val center = Offset(this.size.width / 2f, this.size.height / 2f)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            val distance = sqrt(dx * dx + dy * dy)
                            val outerRadius = this.size.width / 2f
                            val innerRadius = outerRadius * 0.4f

                            // Check if tap is within the ring area
                            if (distance >= innerRadius && distance <= outerRadius) {
                                val angle = atan2(dy, dx) * 180 / PI
                                val normalizedAngle = if (angle < 0) angle + 360 else angle

                                val gapDegrees = 8f
                                val sectorDegrees = 90f - gapDegrees
                                val halfSector = sectorDegrees / 2

                                // Check which sector was tapped based on the new positions
                                when {
                                    // Up sector: 270° ± halfSector
                                    normalizedAngle >= (270 - halfSector) && normalizedAngle <= (270 + halfSector) -> onUp()
                                    // Right sector: 0° ± halfSector (handle wraparound)
                                    normalizedAngle >= (360 - halfSector) || normalizedAngle <= halfSector -> onRight()
                                    // Down sector: 90° ± halfSector  
                                    normalizedAngle >= (90 - halfSector) && normalizedAngle <= (90 + halfSector) -> onDown()
                                    // Left sector: 180° ± halfSector
                                    normalizedAngle >= (180 - halfSector) && normalizedAngle <= (180 + halfSector) -> onLeft()
                                    // If in gap area, do nothing
                                }
                            }
                        }
                    }
                }
        ) {
            val canvasSize = scaledSize.toPx()
            val centerX = canvasSize / 2
            val centerY = canvasSize / 2
            val outerRadius = canvasSize / 2
            val innerRadius = outerRadius * 0.4f

            // Function to draw a sector
            fun DrawScope.drawSector(startAngle: Float, sweepAngle: Float) {
                val path = Path().apply {
                    // Outer arc
                    arcTo(
                        rect = Rect(
                            left = centerX - outerRadius,
                            top = centerY - outerRadius,
                            right = centerX + outerRadius,
                            bottom = centerY + outerRadius
                        ),
                        startAngleDegrees = startAngle,
                        sweepAngleDegrees = sweepAngle,
                        forceMoveTo = false
                    )

                    // Line to inner arc
                    val endAngleRad = (startAngle + sweepAngle) * PI / 180
                    lineTo(
                        centerX + innerRadius * cos(endAngleRad).toFloat(),
                        centerY + innerRadius * sin(endAngleRad).toFloat()
                    )

                    // Inner arc (reverse direction)
                    arcTo(
                        rect = Rect(
                            left = centerX - innerRadius,
                            top = centerY - innerRadius,
                            right = centerX + innerRadius,
                            bottom = centerY + innerRadius
                        ),
                        startAngleDegrees = startAngle + sweepAngle,
                        sweepAngleDegrees = -sweepAngle,
                        forceMoveTo = false
                    )

                    close()
                }

                // Draw filled sector
                drawPath(path, color = if (!isCalibrationMode) Color(0xFFF5F5F5) else Color.White)
                // Draw border
                drawPath(
                    path,
                    color = if (!isCalibrationMode) Color(0xFFE0E0E0) else borderColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }

            // Draw the four sectors (full 90° each, no gaps)
            // Up sector (270°)
            drawSector(225f, 90f)
            // Right sector (0°)
            drawSector(-45f, 90f)
            // Down sector (90°)
            drawSector(45f, 90f)
            // Left sector (180°)
            drawSector(135f, 90f)
        }

        // Overlay icons on each sector
        val arrowTint = if (!isCalibrationMode) Color(0xFFCCCCCC) else Color(0xFF666666)
        
        // Up arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Up",
            tint = arrowTint,
            modifier = Modifier
                .offset(y = (-scaledSize * 0.33f))
                .size(if (deviceType == DeviceType.TABLET) 32.dp else 24.dp)
        )

        // Down arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Down",
            tint = arrowTint,
            modifier = Modifier
                .offset(y = (scaledSize * 0.33f))
                .size(if (deviceType == DeviceType.TABLET) 32.dp else 24.dp)
        )

        // Left arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Left",
            tint = arrowTint,
            modifier = Modifier
                .offset(x = (-scaledSize * 0.33f))
                .size(if (deviceType == DeviceType.TABLET) 32.dp else 24.dp)
        )

        // Right arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Right",
            tint = arrowTint,
            modifier = Modifier
                .offset(x = (scaledSize * 0.33f))
                .size(if (deviceType == DeviceType.TABLET) 32.dp else 24.dp)
        )

        // Center circle with text
        Box(
            modifier = Modifier
                .size(scaledSize * 0.3f)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pan/ Tilt",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = if (deviceType == DeviceType.TABLET) 14.sp else 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ConfigurationControlPanel(
    onSavePreset: (() -> Unit)? = null,
    onDeletePreset: (() -> Unit)? = null,
    isCalibrationMode: Boolean = false
) {
    val deviceType = rememberDeviceType()
    val padding = if (deviceType == DeviceType.TABLET) 48.dp else 36.dp
    val spacing = if (deviceType == DeviceType.TABLET) 16.dp else 12.dp
    val buttonHeight = if (deviceType == DeviceType.TABLET) 48.dp else 36.dp
    val saveButtonWidth = if (deviceType == DeviceType.TABLET) 190.dp else 142.dp
    val deleteButtonWidth = if (deviceType == DeviceType.TABLET) 170.dp else 127.dp
    val fontSize = if (deviceType == DeviceType.TABLET) 14.sp else 12.sp
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .padding(padding),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(saveButtonWidth)
                .height(buttonHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(if (!isCalibrationMode) Color(0xFFE0E0E0) else Color(0xFF2A2A2A))
                .border(
                    width = 1.dp,
                    color = if (!isCalibrationMode) Color(0xFFB0B0B0) else Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(enabled = isCalibrationMode) { 
                    if (isCalibrationMode) {
                        println("Save Preset button pressed")
                        onSavePreset?.invoke()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Save Preset",
                style = TextStyle(
                    color = if (!isCalibrationMode) Color(0xFF666666) else Color(0xFFFFFFFF),
                    fontSize = fontSize,
                    fontWeight = FontWeight(500),
                    fontFamily = FontFamily.SansSerif
                )
            )
        }
        Box(
            modifier = Modifier
                .width(deleteButtonWidth)
                .height(buttonHeight)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = if (!isCalibrationMode) Color(0xFFB0B0B0) else Color(0xFF737373),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(if (!isCalibrationMode) Color(0xFFE8E8E8) else Color.White)
                .clickable(enabled = isCalibrationMode) { 
                    if (isCalibrationMode) {
                        println("Delete button pressed")
                        onDeletePreset?.invoke()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Delete",
                style = TextStyle(
                    color = if (!isCalibrationMode) Color(0xFF666666) else Color(0xFF737373),
                    fontSize = fontSize,
                    fontWeight = FontWeight(500),
                    fontFamily = FontFamily.SansSerif
                )
            )
        }
    }
}

@Composable
fun DPadLayout(
    onSavePreset: (() -> Unit)? = null,
    onDeletePreset: (() -> Unit)? = null,
    deviceIpAddress: String? = null,
    isCalibrationMode: Boolean = false
) {
    val TAG = "DPad Layout"
    val coroutineScope = rememberCoroutineScope()
    val ptzClient = remember(deviceIpAddress) { PTZClient(deviceIpAddress) }

    // Initialize PTZ client
    LaunchedEffect(Unit) {
        try {
            ptzClient.init()
            Log.i(TAG, "PTZ client initialized for movement control")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize PTZ client", e)
        }
    }

    // Function to handle movement commands
    fun handleMovement(direction: String) {
        if (isCalibrationMode) {
            coroutineScope.launch {
                try {
                    Log.i(TAG, "$direction movement initiated")
                    val currentMode = if (isCalibrationMode) 1 else 0
                    val success = ptzClient.controlMovement(direction, 2, currentMode)
                    if (success) {
                        Log.i(TAG, "$direction movement completed successfully")
                    } else {
                        Log.w(TAG, "$direction movement failed")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error during $direction movement", e)
                }
            }
        } else {
            Log.w(TAG, "Movement not allowed in runtime mode")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    )
    {
        DPadController(
            onUp = {
                println("Up pressed")
                handleMovement("u")
            },
            onDown = {
                println("Down pressed")
                handleMovement("d")
            },
            onLeft = {
                println("Left pressed")
                handleMovement("l")
            },
            onRight = {
                println("Right pressed")
                handleMovement("r")
            },
            isCalibrationMode = isCalibrationMode
        )

        ConfigurationControlPanel(
            onSavePreset = onSavePreset,
            onDeletePreset = onDeletePreset,
            isCalibrationMode = isCalibrationMode
        )
    }
}