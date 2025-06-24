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
echo "   Option 1 (Easy): Double-click C:\\cafezinho\\android-v2\\install-apk.bat"
echo "   Option 2 (Manual): cd C:\\cafezinho\\android-v2 && adb install app-debug.apk"
echo ""
echo "⚡ Total workflow: WSL Build → Windows Copy → Device Install"