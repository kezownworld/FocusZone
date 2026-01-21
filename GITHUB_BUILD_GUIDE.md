# 🚀 GitHub Actions Build Guide - Focus Zone

## What is GitHub Actions?
GitHub Actions is a **FREE** cloud build service that automatically builds your Android APK without needing Android Studio or any local setup!

---

## 📋 Step-by-Step Instructions

### **Step 1: Create a GitHub Account** (if you don't have one)
1. Go to https://github.com
2. Click "Sign up"
3. Follow the registration process (it's free!)

---

### **Step 2: Create a New Repository**

1. **Go to:** https://github.com/new
2. **Repository name:** `FocusZone` (or any name you like)
3. **Description:** "Focus Zone - Productivity app for Android"
4. **Visibility:** 
   - ✅ **Public** (recommended - free builds)
   - Or **Private** (also works, but limited free minutes)
5. **DO NOT** check "Add a README file" (we already have one)
6. Click **"Create repository"**

---

### **Step 3: Upload Your Code to GitHub**

You have **two options**:

#### **Option A: Using Git Command Line** (Recommended)

1. **Install Git** (if not installed):
   - Download from: https://git-scm.com/download/win
   - Install with default settings

2. **Open Command Prompt** in your FocusZone folder:
   ```bash
   cd C:\Users\Kez\.gemini\antigravity\scratch\FocusZone
   ```

3. **Initialize Git and push:**
   ```bash
   git init
   git add .
   git commit -m "Initial commit - Focus Zone app"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/FocusZone.git
   git push -u origin main
   ```
   
   Replace `YOUR_USERNAME` with your actual GitHub username!

#### **Option B: Using GitHub Web Interface** (Easier)

1. **Zip your FocusZone folder:**
   - Right-click on `FocusZone` folder
   - Send to → Compressed (zipped) folder

2. **Go to your repository** on GitHub

3. **Click "uploading an existing file"** link

4. **Drag and drop** all files from the FocusZone folder
   - Or click "choose your files" and select all

5. **Scroll down** and click **"Commit changes"**

---

### **Step 4: The Build Happens Automatically!** ✨

Once you push/upload your code:

1. GitHub Actions will **automatically start building** your APK
2. Go to your repository page
3. Click on the **"Actions"** tab at the top
4. You'll see a workflow running (yellow dot = building, green check = done)
5. Wait 5-10 minutes for the build to complete

---

### **Step 5: Download Your APK**

Once the build is complete (green checkmark ✅):

1. Click on the completed workflow run
2. Scroll down to **"Artifacts"** section
3. Click on **"focus-zone-debug"** to download
4. Extract the ZIP file
5. You'll find **`app-debug.apk`** inside!

---

## 📱 Install the APK on Your Phone

### **Method 1: USB Transfer**
1. Connect your phone to PC via USB
2. Copy `app-debug.apk` to your phone
3. Open the APK file on your phone
4. Allow installation from unknown sources if prompted
5. Install!

### **Method 2: Cloud Transfer**
1. Upload APK to Google Drive / Dropbox
2. Download on your phone
3. Install from Downloads folder

---

## 🔄 Making Changes and Rebuilding

Whenever you want to update your app:

1. **Make changes** to your code locally
2. **Push to GitHub:**
   ```bash
   git add .
   git commit -m "Description of changes"
   git push
   ```
3. **GitHub Actions automatically rebuilds** the APK
4. **Download** the new APK from Actions tab

---

## 🎯 Quick Commands Reference

```bash
# First time setup
cd C:\Users\Kez\.gemini\antigravity\scratch\FocusZone
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/FocusZone.git
git push -u origin main

# After making changes
git add .
git commit -m "Your change description"
git push
```

---

## 🆘 Troubleshooting

### "Git is not recognized"
- Install Git from: https://git-scm.com/download/win
- Restart Command Prompt after installation

### "Permission denied"
- You need to authenticate with GitHub
- Use GitHub Desktop (easier): https://desktop.github.com/
- Or set up SSH keys: https://docs.github.com/en/authentication

### "Build failed"
- Check the Actions tab for error messages
- Usually means a code syntax error
- The workflow file I created should work perfectly!

### "Can't install APK on phone"
- Enable "Install from unknown sources" in phone settings
- On newer Android: Settings → Apps → Special access → Install unknown apps

---

## 💡 Pro Tips

1. **Enable Actions:** Make sure GitHub Actions is enabled in your repository settings
2. **Watch the build:** Click on Actions tab to see real-time build progress
3. **Build on demand:** Click "Actions" → "Build Android APK" → "Run workflow" to manually trigger a build
4. **Create releases:** Tag your commits to create official releases with downloadable APKs

---

## 🎉 Benefits of GitHub Actions

✅ **No local setup** - Builds in the cloud  
✅ **Free** - Unlimited builds for public repos  
✅ **Automatic** - Builds on every push  
✅ **Fast** - Usually completes in 5-10 minutes  
✅ **Reliable** - Consistent build environment  
✅ **Shareable** - Anyone can download your APK  

---

## 📞 Need Help?

If you get stuck at any step, just let me know which step and I'll help you through it!

**Ready to start?** Begin with Step 1! 🚀
