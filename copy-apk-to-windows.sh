#!/bin/bash

echo "📱 Copy APK to Windows - WSL Bridge"
echo "=================================="

# Paths
WSL_APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
WINDOWS_TARGET="/mnt/c/cafezinho/android-v2/"

echo "🔍 Checking APK..."
if [ ! -f "$WSL_APK_PATH" ]; then
    echo "❌ APK not found! Building first..."
    ./build-fast.sh
fi

echo "📂 Checking Windows target directory..."
if [ ! -d "$WINDOWS_TARGET" ]; then
    echo "📁 Creating Windows directory..."
    mkdir -p "$WINDOWS_TARGET"
fi

echo "📱 Copying APK to Windows..."
cp "$WSL_APK_PATH" "$WINDOWS_TARGET"

if [ $? -eq 0 ]; then
    echo "✅ APK copied successfully!"
    echo "📍 Windows location: C:\\cafezinho\\android-v2\\app-debug.apk"
    echo ""
    echo "🔧 Windows install command:"
    echo "   cd C:\\cafezinho\\android-v2"
    echo "   adb install app-debug.apk"
    echo ""
    
    # Create Windows batch file for easy install
    cat > "$WINDOWS_TARGET/install-apk.bat" << 'EOF'
@echo off
echo 📱 Installing Cafezinho APK...
echo ============================

if not exist "app-debug.apk" (
    echo ❌ APK not found! Run copy-apk-to-windows.sh in WSL first.
    pause
    exit /b 1
)

echo 🔍 Checking connected devices...
adb devices

echo.
echo 🚀 Installing APK (with signature handling)...

REM Try normal install first
adb install app-debug.apk

if %errorlevel% == 0 (
    echo.
    echo ✅ Installation successful!
    goto START_APP
)

echo.
echo ⚠️  Normal install failed, trying to reinstall...
adb install -r app-debug.apk

if %errorlevel% == 0 (
    echo.
    echo ✅ Reinstallation successful!
    goto START_APP
)

echo.
echo ⚠️  Reinstall failed, uninstalling old version first...
adb uninstall com.rcdnc.cafezinho.debug
adb install app-debug.apk

if %errorlevel% == 0 (
    echo.
    echo ✅ Fresh installation successful!
    goto START_APP
) else (
    echo.
    echo ❌ All installation methods failed!
    echo 💡 Check device connection and USB debugging
    goto END
)

:START_APP
echo 📱 Starting app...
adb shell am start -n com.rcdnc.cafezinho/.MainActivity

:END
echo.
pause
EOF

    echo "🎯 Created install-apk.bat for easy Windows installation"
    echo "📁 Just double-click: C:\\cafezinho\\android-v2\\install-apk.bat"
else
    echo "❌ Copy failed!"
fi