@echo off
echo ========================================
echo Focus Zone - Install APK
echo ========================================
echo.

REM Check if adb is available
echo Checking for ADB...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: ADB is not installed or not in PATH
    echo.
    echo ADB is part of Android SDK Platform Tools
    echo Download from: https://developer.android.com/studio/releases/platform-tools
    echo.
    pause
    exit /b 1
)
echo ADB found!
echo.

REM Check if APK exists
if not exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ERROR: APK not found!
    echo Please build the app first by running: build.bat
    echo.
    pause
    exit /b 1
)

REM Check for connected devices
echo Checking for connected devices...
adb devices
echo.

REM Install APK
echo Installing Focus Zone...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Installation failed!
    echo Make sure:
    echo 1. USB debugging is enabled on your device
    echo 2. Device is connected via USB
    echo 3. You authorized the computer on your device
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS!
echo ========================================
echo Focus Zone has been installed on your device!
echo.
pause
