# 🔧 Method 1: Local Build Setup (Java 17 + Android SDK)

## Overview
This method lets you build the APK directly on your computer without needing Android Studio. You'll have full control and can build anytime offline.

**Time Required:** ~30-45 minutes (mostly downloading)  
**Disk Space:** ~3-4 GB  
**Difficulty:** Medium (follow steps carefully)

---

## 📋 What You'll Install

1. **Java 17** (JDK) - Required to run Gradle
2. **Android SDK Command-Line Tools** - Required to build Android apps
3. **SDK Components** - Platform tools and build tools

---

## Step 1: Install Java 17 (JDK)

### Download Java 17

1. **Go to:** https://adoptium.net/temurin/releases/
2. **Select:**
   - Version: **17 - LTS**
   - Operating System: **Windows**
   - Architecture: **x64**
   - Package Type: **JDK**
   - Image Type: **.msi** (installer)

3. **Click Download** (file will be ~180 MB)

### Install Java 17

1. **Run the downloaded `.msi` file**
2. **Important:** During installation, make sure these are checked:
   - ✅ **Set JAVA_HOME variable**
   - ✅ **JavaSoft (Oracle) registry keys**
   - ✅ **Add to PATH**

3. **Click Next** through the installation
4. **Wait** for installation to complete

### Verify Java Installation

1. **Open a NEW Command Prompt** (important - close old ones)
2. **Run:**
   ```bash
   java -version
   ```

3. **You should see:**
   ```
   openjdk version "17.0.x"
   OpenJDK Runtime Environment Temurin-17...
   ```

4. **If you see Java 1.8 instead:**
   - You need to update your PATH
   - Or uninstall Java 8 first
   - See troubleshooting section below

---

## Step 2: Download Android SDK Command-Line Tools

### Download SDK Tools

1. **Go to:** https://developer.android.com/studio#command-tools

2. **Scroll down** to "Command line tools only"

3. **Download:** `commandlinetools-win-XXXXXXX_latest.zip`
   - File size: ~150 MB

### Extract SDK Tools

1. **Create a folder:** `C:\Android\Sdk`
   - You can choose a different location, but remember it!

2. **Extract the downloaded ZIP** to a temporary location

3. **Inside the extracted folder**, you'll find a `cmdline-tools` folder

4. **Create this structure:**
   ```
   C:\Android\Sdk\
   └── cmdline-tools\
       └── latest\
           ├── bin\
           ├── lib\
           └── ...
   ```

5. **Move the contents** of the extracted `cmdline-tools` folder into:
   ```
   C:\Android\Sdk\cmdline-tools\latest\
   ```

---

## Step 3: Set Environment Variables

### Set ANDROID_HOME

1. **Press** `Windows Key + R`
2. **Type:** `sysdm.cpl` and press Enter
3. **Click** "Advanced" tab
4. **Click** "Environment Variables" button

5. **Under "System variables"** (bottom section):
   - Click **"New"**
   - Variable name: `ANDROID_HOME`
   - Variable value: `C:\Android\Sdk`
   - Click **OK**

### Update PATH

1. **Still in Environment Variables window**
2. **Find "Path"** in System variables
3. **Click "Edit"**
4. **Click "New"** and add these three entries:
   ```
   C:\Android\Sdk\cmdline-tools\latest\bin
   C:\Android\Sdk\platform-tools
   C:\Android\Sdk\emulator
   ```
5. **Click OK** on all windows

### Verify Environment Variables

1. **Open a NEW Command Prompt**
2. **Run:**
   ```bash
   echo %ANDROID_HOME%
   ```
   Should show: `C:\Android\Sdk`

---

## Step 4: Install SDK Components

### Accept Licenses

1. **Open Command Prompt as Administrator**
   - Right-click Command Prompt → Run as administrator

2. **Navigate to SDK tools:**
   ```bash
   cd C:\Android\Sdk\cmdline-tools\latest\bin
   ```

3. **Accept all licenses:**
   ```bash
   sdkmanager --licenses
   ```

4. **Type `y` and press Enter** for each license (there are several)

### Install Required Components

**Run these commands** (one at a time):

```bash
sdkmanager "platform-tools"
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"
sdkmanager "cmdline-tools;latest"
```

**Wait** for each to download and install (this will take 10-15 minutes)

### Verify Installation

```bash
sdkmanager --list_installed
```

You should see:
- platform-tools
- platforms;android-34
- build-tools;34.0.0

---

## Step 5: Build Your APK!

### Navigate to Project

```bash
cd C:\Users\Kez\.gemini\antigravity\scratch\FocusZone
```

### Build Debug APK

**Option A: Using the build script (easiest)**
```bash
build.bat
```

**Option B: Using Gradle directly**
```bash
gradlew.bat assembleDebug
```

### Wait for Build

- **First build:** 5-10 minutes (downloads dependencies)
- **Subsequent builds:** 1-2 minutes

### Find Your APK

After successful build:
```
app\build\outputs\apk\debug\app-debug.apk
```

---

## Step 6: Install APK on Your Phone

### Enable USB Debugging on Phone

1. **Go to Settings** → About Phone
2. **Tap "Build Number" 7 times** (enables Developer Options)
3. **Go back** → Developer Options
4. **Enable "USB Debugging"**

### Install via USB

1. **Connect phone to PC** via USB cable
2. **On phone:** Allow USB debugging when prompted
3. **On PC, run:**
   ```bash
   install.bat
   ```

**Or manually:**
```bash
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

---

## 🎯 Quick Command Reference

```bash
# Build APK
cd C:\Users\Kez\.gemini\antigravity\scratch\FocusZone
gradlew.bat assembleDebug

# Install on device
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Clean build (if needed)
gradlew.bat clean

# Build release APK (unsigned)
gradlew.bat assembleRelease

# Check connected devices
adb devices
```

---

## 🆘 Troubleshooting

### "Java version is still 1.8"

**Solution 1: Update PATH order**
1. Open Environment Variables
2. Edit PATH
3. Move Java 17 path to the TOP
4. Remove Java 8 paths if present

**Solution 2: Uninstall Java 8**
1. Control Panel → Programs → Uninstall
2. Remove old Java versions
3. Reinstall Java 17

### "ANDROID_HOME is not set"

1. Close ALL Command Prompt windows
2. Open a NEW Command Prompt
3. Run: `echo %ANDROID_HOME%`
4. If still empty, recheck Step 3

### "sdkmanager is not recognized"

1. Verify folder structure:
   ```
   C:\Android\Sdk\cmdline-tools\latest\bin\sdkmanager.bat
   ```
2. Make sure PATH includes:
   ```
   C:\Android\Sdk\cmdline-tools\latest\bin
   ```

### "Build failed: SDK location not found"

**Create local.properties file:**
1. In FocusZone folder, create: `local.properties`
2. Add this line:
   ```
   sdk.dir=C\:\\Android\\Sdk
   ```
   (Note the double backslashes!)

### "License not accepted"

```bash
cd C:\Android\Sdk\cmdline-tools\latest\bin
sdkmanager --licenses
```
Type `y` for all licenses

### "ADB not found"

```bash
cd C:\Android\Sdk\cmdline-tools\latest\bin
sdkmanager "platform-tools"
```

Then add to PATH:
```
C:\Android\Sdk\platform-tools
```

### Build is very slow

**First build is always slow** (downloads dependencies)

To speed up future builds, add to `gradle.properties`:
```
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
```

---

## ✅ Verification Checklist

Before building, verify:

- [ ] Java 17 installed: `java -version` shows 17.x.x
- [ ] ANDROID_HOME set: `echo %ANDROID_HOME%` shows SDK path
- [ ] SDK tools installed: `sdkmanager --list_installed` shows components
- [ ] Licenses accepted: `sdkmanager --licenses` shows all accepted
- [ ] In project folder: `cd C:\Users\Kez\.gemini\antigravity\scratch\FocusZone`

---

## 💡 Benefits of Local Build

✅ **Offline building** - No internet needed after setup  
✅ **Fast builds** - No upload/download time  
✅ **Full control** - Customize build process  
✅ **Privacy** - Code stays on your machine  
✅ **Learning** - Understand Android build process  

---

## 📦 Disk Space Breakdown

- Java 17: ~300 MB
- Android SDK tools: ~150 MB
- Platform tools: ~50 MB
- Android 34 platform: ~70 MB
- Build tools: ~80 MB
- Gradle cache (after first build): ~500 MB
- **Total: ~3-4 GB**

---

## 🔄 Updating Components

To update SDK components:
```bash
sdkmanager --update
```

To update Gradle wrapper:
```bash
gradlew.bat wrapper --gradle-version=8.4
```

---

## 🎓 Next Steps After Setup

Once you can build successfully:

1. **Make changes** to the code
2. **Run:** `gradlew.bat assembleDebug`
3. **Install:** `adb install -r app\build\outputs\apk\debug\app-debug.apk`
4. **Test** on your device

---

## ❓ Still Having Issues?

If you get stuck:

1. **Check the error message** carefully
2. **Google the specific error** (usually has solutions)
3. **Verify all paths** are correct
4. **Try restarting** Command Prompt
5. **Restart your computer** (sometimes needed for PATH changes)

**Common issues are usually:**
- Wrong Java version
- PATH not updated
- SDK not in correct folder structure
- Licenses not accepted

---

**Ready to start?** Begin with Step 1! 🚀

Need help with any specific step? Just ask!
