# PTZ Control Android App

A modern Android application for controlling PTZ (Pan-Tilt-Zoom) cameras with preset management and real-time movement controls.

## Features

### 📹 Camera Control
- **Directional Movement**: Control camera pan/tilt using intuitive D-Pad interface
- **Timed Movement**: Each direction command moves the camera for 2 seconds
- **Real-time Response**: Immediate camera control with visual feedback

### 🎯 Preset Management
- **View Presets**: Browse all saved camera presets with their custom names
- **Create Presets**: Add new presets with custom names via dialog interface
- **Save Presets**: Convert temporary presets to permanent ones with API integration
- **Delete Presets**: Remove unwanted presets with confirmation dialog
- **Reload Presets**: Refresh preset list to sync with latest server data

### 🔄 Smart Interface
- **Auto-reload**: Refresh button fetches latest presets from server
- **Error Handling**: User-friendly error messages for failed operations
- **Visual Feedback**: Selected presets highlighted, temporary presets clearly marked
- **Responsive Design**: Optimized for tablet and phone interfaces

## User Experience

### Getting Started
1. **Launch App**: Camera presets load automatically on startup
2. **Select Preset**: Tap any preset button to select it
3. **Control Camera**: Use D-Pad to move camera in any direction

### Managing Presets
1. **Add New**: Tap the "+" button to create a temporary preset
2. **Name Preset**: Select temporary preset and tap "Save Preset" button
3. **Enter Name**: Type custom name in dialog and confirm
4. **Delete Preset**: Select preset and tap "Delete" button, confirm in dialog

### Camera Movement
- **Up/Down**: Pan camera vertically
- **Left/Right**: Tilt camera horizontally  
- **Duration**: Each movement lasts 2 seconds
- **Feedback**: Console logs confirm movement commands

### Refresh Data
- **Reload Button**: Top-right reload icon fetches latest presets
- **Auto-sync**: Keeps local data synchronized with server

## Technical Details

- **Platform**: Android (Kotlin + Jetpack Compose)
- **API Integration**: RESTful APIs for preset and movement control
- **Architecture**: MVVM pattern with state management
- **UI Framework**: Material Design 3 components
- **Network**: Ktor client for HTTP communication

## API Endpoints

- `GET /fetchPresets` - Retrieve saved presets
- `POST /setPreset` - Save new preset
- `POST /deletePreset` - Remove preset  
- `POST /controlMovement` - Control camera movement

---

*Built for seamless PTZ camera control with modern Android development practices.*