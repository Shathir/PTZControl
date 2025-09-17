package com.outdu.ptzcontrol.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.outdu.ptzcontrol.R
import com.outdu.ptzcontrol.client.PTZClient
import kotlinx.coroutines.selects.select


@Composable
fun PresetNameDialog(
    isVisible: Boolean,
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textFieldValue by remember(currentName) { mutableStateOf(currentName) }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Enter Preset Name")
            },
            text = {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text("Preset Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (textFieldValue.isNotBlank()) {
                            onConfirm(textFieldValue.trim())
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    isVisible: Boolean,
    presetName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Delete Preset")
            },
            text = {
                Text("Are you sure you want to delete the preset \"$presetName\"? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = onConfirm
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ErrorDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(title)
            },
            text = {
                Text(message)
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun PresetRow(
    savePresetTrigger: Int = 0,
    deletePresetTrigger: Int = 0,
    reloadPresetsTrigger: Int = 0
){

    val TAG = "Preset Row"
    var isSelected by remember { mutableStateOf(0) }

    var presets by remember { mutableStateOf<List<PTZClient.Preset>>(emptyList()) }
    var tempPresets by remember { mutableStateOf<List<PTZClient.Preset>>(emptyList()) }
    val ptzClient = remember { PTZClient() }
    
    // Dialog state
    var showNameDialog by remember { mutableStateOf(false) }
    var pendingPreset by remember { mutableStateOf<PTZClient.Preset?>(null) }
    var presetToSave by remember { mutableStateOf<Pair<Int, String>?>(null) }
    
    // Delete dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var presetToDelete by remember { mutableStateOf<Pair<Int, String>?>(null) }

    // Function to load/reload presets
    suspend fun loadPresets() {
        try {
            ptzClient.init()
            val fetchedPresets = ptzClient.fetchPresets()
            presets = fetchedPresets
            tempPresets = fetchedPresets
            Log.i(TAG, "Loaded ${presets.size} presets: ${presets.map { it.name }}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch presets", e)
            presets = emptyList()
            tempPresets = emptyList()
        }
    }

    LaunchedEffect(Unit) {
        loadPresets()
    }

    // Combined list of presets (saved + temporary)
    val allPresets = tempPresets

    // Function to save the currently selected preset
    fun saveSelectedPreset() {
        if (isSelected >= 0 && isSelected < allPresets.size) {
            val selectedPreset = allPresets[isSelected]
            // Check if it's a temporary preset (negative ID)
            if (selectedPreset.id < 0) {
                pendingPreset = selectedPreset
                showNameDialog = true
            } else {
                Log.i(TAG, "Preset already saved: ${selectedPreset.name}")
            }
        }
    }

    // Function to delete the currently selected preset
    fun deleteSelectedPreset() {
        if (isSelected >= 0 && isSelected < allPresets.size) {
            val selectedPreset = allPresets[isSelected]
            // Only allow deleting saved presets (positive ID)
            if (selectedPreset.id > 0) {
                showDeleteDialog = true
                Log.i(TAG, "Requesting delete for preset: ${selectedPreset.name}")
            } else {
                // For temporary presets, just remove them locally
                tempPresets = tempPresets.filter { it.id != selectedPreset.id }
                if (isSelected >= tempPresets.size) {
                    isSelected = if (tempPresets.isNotEmpty()) tempPresets.size - 1 else 0
                }
                Log.i(TAG, "Removed temporary preset: ${selectedPreset.name}")
            }
        }
    }

    // Handle save preset request from parent
    LaunchedEffect(savePresetTrigger) {
        if (savePresetTrigger > 0) {
            Log.d(TAG, "Save preset triggered: $savePresetTrigger")
            saveSelectedPreset()
        }
    }

    // Handle delete preset request from parent
    LaunchedEffect(deletePresetTrigger) {
        if (deletePresetTrigger > 0) {
            Log.d(TAG, "Delete preset triggered: $deletePresetTrigger")
            deleteSelectedPreset()
        }
    }

    // Handle reload presets request from parent
    LaunchedEffect(reloadPresetsTrigger) {
        if (reloadPresetsTrigger > 0) {
            Log.d(TAG, "Reload presets triggered: $reloadPresetsTrigger")
            loadPresets()
            // Reset selection to first preset if available
            isSelected = if (tempPresets.isNotEmpty()) 0 else 0
        }
    }

    // Handle API call for saving preset
    LaunchedEffect(presetToSave) {
        presetToSave?.let { (id, name) ->
            try {
                val success = ptzClient.setPreset(id, name)
                if (success) {
                    Log.i(TAG, "Preset saved successfully: $name with ID: $id")
                    // Find and update the preset in tempPresets
                    val updatedPreset = PTZClient.Preset(id = id, name = name)
                    tempPresets = tempPresets.map { preset ->
                        if (preset.name == name && preset.id < 0) updatedPreset else preset 
                    }
                    // Add to permanent presets list
                    presets = presets + updatedPreset
                } else {
                    Log.e(TAG, "Failed to save preset: $name")
                    // Remove failed preset from tempPresets
                    tempPresets = tempPresets.filter { preset -> 
                        !(preset.name == name && preset.id < 0) 
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving preset: $name", e)
                // Remove failed preset from tempPresets
                tempPresets = tempPresets.filter { preset -> 
                    !(preset.name == name && preset.id < 0) 
                }
            }
            presetToSave = null
        }
    }

    // Handle API call for deleting preset
    LaunchedEffect(presetToDelete) {
        presetToDelete?.let { (presetNumber, presetName) ->
            try {
                val (success, message) = ptzClient.deletePreset(presetNumber, presetName)
                if (success) {
                    Log.i(TAG, "Preset deleted successfully: $presetName")
                    // Remove from both lists
                    presets = presets.filter { it.id != presetNumber }
                    tempPresets = tempPresets.filter { it.id != presetNumber }
                    // Adjust selection if needed
                    if (isSelected >= tempPresets.size) {
                        isSelected = if (tempPresets.isNotEmpty()) tempPresets.size - 1 else 0
                    }
                } else {
                    Log.e(TAG, "Failed to delete preset: $presetName - $message")
                    errorMessage = message ?: "Failed to delete preset"
                    showErrorDialog = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting preset: $presetName", e)
                errorMessage = "Network error: ${e.message}"
                showErrorDialog = true
            }
            presetToDelete = null
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    )
    {

        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            items(allPresets.size) { index ->
                val preset = allPresets[index]
                Box(
                    modifier = Modifier.width(100.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if(isSelected == index) Color.Black else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF737373),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            isSelected = index
                            Log.i(TAG, "Selected preset: ${preset.name} (ID: ${preset.id})")
                        },
                    contentAlignment = Alignment.Center
                )
                {
                    Text(
                        text = preset.name,
                        style = TextStyle(
                            color = if(isSelected == index) Color.White else Color(0xFF2A2A2A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight(400),
                            fontFamily = FontFamily.SansSerif
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Box(

            modifier = Modifier.size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = Color(0xFF737373),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    Log.i(TAG, "Add button clicked")
                    // Generate a temporary ID (negative to distinguish from real ones)
                    val tempId = -(tempPresets.size + 1)
                    val newPreset = PTZClient.Preset(
                        id = tempId,
                        name = "New Preset ${tempPresets.size - presets.size + 1}"
                    )
                    tempPresets = tempPresets + newPreset
                    isSelected = tempPresets.size - 1 // Select the new preset
                    Log.i(TAG, "Added temporary preset: ${newPreset.name}")
                },
            contentAlignment = Alignment.Center
        )
        {
            Icon(
                painter = painterResource(R.drawable.plus),
                contentDescription = "Add",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
                    .align(Alignment.Center)
            )
        }

    }

    // Show dialog when needed
    PresetNameDialog(
        isVisible = showNameDialog,
        currentName = pendingPreset?.name ?: "",
        onDismiss = {
            showNameDialog = false
            pendingPreset = null
        },
        onConfirm = { newName ->
            showNameDialog = false
            pendingPreset?.let { preset ->
                // Generate a new positive ID for the saved preset
                val newId = (presets.maxOfOrNull { it.id } ?: 0) + 1
                Log.i(TAG, "Attempting to save preset: $newName with ID: $newId")
                
                // Update the temporary preset with the new name
                tempPresets = tempPresets.map { 
                    if (it.id == preset.id) it.copy(name = newName) else it 
                }
                
                // Trigger API call
                presetToSave = Pair(newId, newName)
            }
            pendingPreset = null
        }
    )

    // Show delete confirmation dialog
    ConfirmDeleteDialog(
        isVisible = showDeleteDialog,
        presetName = if (isSelected >= 0 && isSelected < allPresets.size) allPresets[isSelected].name else "",
        onDismiss = {
            showDeleteDialog = false
        },
        onConfirm = {
            showDeleteDialog = false
            if (isSelected >= 0 && isSelected < allPresets.size) {
                val selectedPreset = allPresets[isSelected]
                presetToDelete = Pair(selectedPreset.id, selectedPreset.name)
            }
        }
    )

    // Show error dialog
    ErrorDialog(
        isVisible = showErrorDialog,
        title = "Delete Error",
        message = errorMessage,
        onDismiss = {
            showErrorDialog = false
            errorMessage = ""
        }
    )
}