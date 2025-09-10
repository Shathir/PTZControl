package com.outdu.ptzcontrol.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.outdu.ptzcontrol.R


/*
Integrate Gstreamer to stream from camera
*/
@Composable
fun CameraStreamLayout() {

    Image(
        painter = painterResource(R.drawable.stream_sample_visible),
        contentDescription = "Sample Image",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
            .padding(48.dp)
    )
}