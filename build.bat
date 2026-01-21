@echo off
echo ========================================
echo Focus Zone - Build Script
echo ========================================
echo.

REM Check if Java is installed
echo [1/5] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install JDK 17 or higher from: https://adoptium.net/
    pause
    exit /b 1
)
echo Java found!
echo.

REM Check if Android SDK is set
echo [2/5] Checking Android SDK...
if not defined ANDROID_HOME (
    echo WARNING: ANDROID_HOME is not set
    echo.
    echo Please set ANDROID_HOME environment variable to your Android SDK location
    echo Example: C:\Users\%USERNAME%\AppData\Local\Android\Sdk
    echo.
    echo You can download Android SDK command-line tools from:
    echo https://developer.android.com/studio#command-tools
    echo.
    pause
    exit /b 1
)
echo Android SDK found at: %ANDROID_HOME%
echo.

REM Make gradlew executable (if needed)
echo [3/5] Setting up Gradle wrapper...
if not exist gradlew.bat (
    echo ERROR: gradlew.bat not found
    echo Downloading Gradle wrapper...
    gradle wrapper
)
echo Gradle wrapper ready!
echo.

REM Build the project
echo [4/5] Building APK...
echo This may take a few minutes on first build...
echo.
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo.

REM Success message
echo [5/5] Build complete!
echo.
echo ========================================
echo SUCCESS!
echo ========================================
echo.
echo APK Location:
echo app\build\outputs\apk\debug\app-debug.apk
echo.
echo To install on your device:
echo 1. Enable USB debugging on your Android device
echo 2. Connect device via USB
echo 3. Run: adb install app\build\outputs\apk\debug\app-debug.apk
echo.
pause
