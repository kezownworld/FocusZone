# Focus Zone Android App

A productivity app that helps you stay focused by blocking calls, notifications, and selected apps during timed focus sessions.

## Features

- ⏱️ **Customizable Timer**: Set focus sessions with hours, minutes, and seconds
- 📞 **Call Blocking**: Automatically reject incoming calls during focus mode
- 🔕 **Notification Blocking**: Block all notifications except from the app itself
- 📱 **App Blocking**: Select specific apps to block and prevent usage during focus sessions
- 🎨 **Beautiful UI**: Glassmorphic design with light and dark themes
- 🌈 **Gradient Backgrounds**: Baby pink, light blue, and white gradients with blur effects

## Requirements

- Android 8.0 (API 26) or higher
- Permissions:
  - Phone permissions (for call blocking)
  - Notification listener access
  - Usage stats access (for app blocking)
  - Overlay permission (for blocking screen)
  - Post notifications (Android 13+)

## Building the Project

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on an Android device or emulator

```bash
./gradlew assembleDebug
```

## Installation

Install the APK on your Android device:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Usage

1. **Grant Permissions**: On first launch, grant all required permissions
2. **Select Apps**: Choose which apps to block during focus sessions
3. **Set Timer**: Use the time pickers to set your focus duration
4. **Start Focus**: Tap "Start Focus" to begin your session
5. **Stay Focused**: The app will block calls, notifications, and selected apps
6. **Force Stop**: If needed, you can force stop the timer early

## Architecture

- **MVVM Pattern**: ViewModels for business logic, Activities for UI
- **Room Database**: Local storage for blocked apps and focus sessions
- **Foreground Service**: Timer runs as a foreground service
- **Broadcast Receivers**: Communication between service and UI
- **Material Design 3**: Modern UI components with custom glassmorphic styling

## Technologies Used

- Kotlin
- Android SDK
- XML Layouts
- Room Database
- Coroutines
- LiveData & ViewModel
- Material Design 3
- DataStore Preferences

## License

This project is created for educational purposes.
