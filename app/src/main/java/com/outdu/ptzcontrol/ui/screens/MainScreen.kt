package com.outdu.ptzcontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.outdu.ptzcontrol.ui.components.CameraStreamLayout
import com.outdu.ptzcontrol.ui.components.DPadLayout
import com.outdu.ptzcontrol.ui.components.InfoCard
import com.outdu.ptzcontrol.ui.components.PresetRow

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {

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
            modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .background(Color.Transparent),
        )
        {
            InfoCard()
        }

        Box(
            modifier.fillMaxWidth()
                .height(60.dp)
                .background(Color.Transparent)
        )
        {
            PresetRow()
        }

        // Top half screen for showing stream
        Box(
            modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(Color.Transparent),
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
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        )
        {
            DPadLayout()
        }

    }
}