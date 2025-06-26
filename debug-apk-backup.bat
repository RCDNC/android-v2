@echo off
echo 🔍 Debugging APK Installation...
echo ================================

echo.
echo 📱 Checking installed packages:
adb shell pm list packages | findstr cafezinho

echo.
echo 🔍 Checking package details:
adb shell dumpsys package com.rcdnc.cafezinho.debug | findstr -A 5 -B 5 "Activity"

echo.
echo 📋 APK info:
adb shell pm dump com.rcdnc.cafezinho.debug | findstr "Package\|codePath\|versionName"

echo.
echo 🚀 Available activities:
adb shell pm dump com.rcdnc.cafezinho.debug | findstr "Activity"

echo.
echo 💡 Trying alternative launch methods:
echo Method 1: Full package name
adb shell monkey -p com.rcdnc.cafezinho.debug -c android.intent.category.LAUNCHER 1

echo.
pause