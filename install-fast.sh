#!/bin/bash

echo "📱 Fast Install Script - Deploy to Device"
echo "========================================"

echo "🔍 Checking connected devices..."
adb devices

echo ""
echo "🚀 Installing with optimizations..."

# Fast install command
./gradlew installDebug \
    --build-cache \
    --configuration-cache \
    --parallel \
    -x lint \
    -x test \
    --daemon

echo ""
if [ $? -eq 0 ]; then
    echo "✅ Install successful!"
    echo "📱 Starting app on device..."
    adb shell am start -n com.rcdnc.cafezinho/.MainActivity
else
    echo "❌ Install failed!"
fi