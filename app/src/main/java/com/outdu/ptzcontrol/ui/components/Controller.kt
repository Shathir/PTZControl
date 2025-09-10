package com.outdu.ptzcontrol.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun DPadController(
    size: Dp = 250.dp,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit
) {
    val sectorColor = Color(0xFFE8E8E8)
    val borderColor = Color(0xFFD0D0D0)
    
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Draw the circular D-Pad with arc sectors
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Handle touch events
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
        ) {
            val canvasSize = size.toPx()
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
                drawPath(path, color = Color.White)
                // Draw border
                drawPath(path, color = borderColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
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
        // Up arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Up",
            tint = Color(0xFF666666),
            modifier = Modifier
                .offset(y = (-size * 0.33f))
                .size(32.dp)
        )
        
        // Down arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Down",
            tint = Color(0xFF666666),
            modifier = Modifier
                .offset(y = (size * 0.33f))
                .size(32.dp)
        )
        
        // Left arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Left",
            tint = Color(0xFF666666),
            modifier = Modifier
                .offset(x = (-size * 0.33f))
                .size(32.dp)
        )
        
        // Right arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Right",
            tint = Color(0xFF666666),
            modifier = Modifier
                .offset(x = (size * 0.33f))
                .size(32.dp)
        )
        
        // Center circle with text
        Box(
            modifier = Modifier
                .size(size * 0.3f)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pan/ Tilt",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    }
}


@Composable
fun DPadLayout() {
    DPadController(
        onUp = { println("Up pressed") },
        onDown = { println("Down pressed") },
        onLeft = { println("Left pressed") },
        onRight = { println("Right pressed") }
    )
}