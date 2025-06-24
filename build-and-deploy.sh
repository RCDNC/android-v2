#!/bin/bash

echo "🚀 Build and Deploy - Complete WSL to Windows Workflow"
echo "====================================================="

echo "📱 Platform: $(uname -s) $(if [ -n "${WSL_DISTRO_NAME}" ]; then echo "(WSL: ${WSL_DISTRO_NAME})"; fi)"
echo ""

# Step 1: Fast Build
echo "⚡ Step 1: Building APK..."
./build-fast.sh

if [ $? -ne 0 ]; then
    echo "❌ Build failed! Stopping."
    exit 1
fi

echo ""

# Step 2: Copy to Windows
echo "📂 Step 2: Copying to Windows..."
./copy-apk-to-windows.sh

echo ""
echo "🎯 COMPLETE WORKFLOW FINISHED!"
echo "=============================="
echo ""
echo "📱 Next steps in Windows:"
echo ""
echo "🎯 AUTOMATIC (Recommended):"
echo "   📁 Double-click: C:\\cafezinho\\android-v2\\install-apk.bat"
echo "   📍 APK: C:\\cafezinho\\android-v2\\app-debug.apk"
echo "   🗑️  Auto-uninstalls + installs + starts app"
echo ""
echo "💡 MANUAL:"
echo "   cd C:\\cafezinho\\android-v2"
echo "   adb uninstall com.rcdnc.cafezinho.debug"
echo "   adb install app-debug.apk"
echo ""
echo "⚡ Total workflow: WSL Build (2s) → Windows Copy → Device Install"