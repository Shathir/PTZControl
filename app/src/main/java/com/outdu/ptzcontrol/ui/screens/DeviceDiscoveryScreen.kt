package com.outdu.ptzcontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.outdu.ptzcontrol.services.OnvifDevice
import com.outdu.ptzcontrol.services.discoverOnvifDevices
import com.outdu.ptzcontrol.ui.components.DeviceCard

@Composable
fun DeviceDiscoveryScreen(
    onDeviceSelected: (OnvifDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    var discoveredDevices by remember { mutableStateOf<List<OnvifDevice>>(emptyList()) }
    var isDiscovering by remember { mutableStateOf(true) }
    var discoveryAttempts by remember { mutableStateOf(0) }

    // Start device discovery when the screen is first composed
    LaunchedEffect(discoveryAttempts) {
        isDiscovering = true
        discoverOnvifDevices { devices ->
            discoveredDevices = devices
            isDiscovering = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Discover ONVIF Devices",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        when {
            isDiscovering -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Searching for ONVIF devices...",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "This may take up to 10 seconds",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            discoveredDevices.isEmpty() -> {
                // No devices found state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "No ONVIF devices found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Make sure your devices are connected to the same network and support ONVIF discovery.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Button(
                            onClick = { 
                                discoveryAttempts += 1
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Search Again")
                        }
                    }
                }
            }
            
            else -> {
                // Devices found state
                Text(
                    text = "Found ${discoveredDevices.size} device${if (discoveredDevices.size != 1) "s" else ""}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(discoveredDevices) { device ->
                        DeviceCard(
                            device = device,
                            onClick = onDeviceSelected
                        )
                    }
                }
                
                // Refresh button
                Button(
                    onClick = { 
                        discoveryAttempts += 1
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Search Again")
                }
            }
        }
    }
}
