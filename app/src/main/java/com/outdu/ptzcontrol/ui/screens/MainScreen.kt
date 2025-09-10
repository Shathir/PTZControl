package com.outdu.ptzcontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.outdu.ptzcontrol.ui.components.CameraStreamLayout

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {

    // Column to fill the complete screen in a vertical stack.
    Column(modifier = Modifier.fillMaxSize())
    {
        // Top half screen for showing stream
        Box(
            modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(Color.Red),
            contentAlignment = Alignment.Center
        )
        {
                CameraStreamLayout()
        }

        // Bottom half screen for showing controls
        Box(
            modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Green),
            contentAlignment = Alignment.Center
        )
        {

        }

    }
}