@echo off
echo ========================================
echo GitHub Quick Setup - Focus Zone
echo ========================================
echo.

REM Check if Git is installed
echo Checking for Git...
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo Git is not installed!
    echo.
    echo Please install Git from: https://git-scm.com/download/win
    echo After installation, run this script again.
    echo.
    pause
    exit /b 1
)
echo Git found!
echo.

REM Get GitHub username
set /p USERNAME="Enter your GitHub username: "
if "%USERNAME%"=="" (
    echo Username cannot be empty!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Setting up Git repository...
echo ========================================
echo.

REM Initialize Git
git init
if %errorlevel% neq 0 (
    echo Failed to initialize Git repository
    pause
    exit /b 1
)

REM Add all files
echo Adding files...
git add .
if %errorlevel% neq 0 (
    echo Failed to add files
    pause
    exit /b 1
)

REM Commit
echo Creating initial commit...
git commit -m "Initial commit - Focus Zone app"
if %errorlevel% neq 0 (
    echo Failed to commit
    pause
    exit /b 1
)

REM Rename branch to main
echo Setting main branch...
git branch -M main

REM Add remote
echo Adding GitHub remote...
git remote add origin https://github.com/%USERNAME%/FocusZone.git

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Go to https://github.com/new
echo 2. Create a repository named: FocusZone
echo 3. Make it Public (for free builds)
echo 4. DO NOT add README or .gitignore
echo 5. Click "Create repository"
echo.
echo Then run this command to push:
echo    git push -u origin main
echo.
echo After pushing, GitHub Actions will automatically build your APK!
echo Check the "Actions" tab on GitHub to see the build progress.
echo.
pause
