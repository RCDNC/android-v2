#!/bin/bash

echo "🚀 Fast Build Script - Multi-Platform Optimized"
echo "=============================================="

echo "📱 Platform: $(uname -s) $(if [ -n "${WSL_DISTRO_NAME}" ]; then echo "(WSL: ${WSL_DISTRO_NAME})"; fi)"
echo "🔧 Building with optimizations..."

# Fast build command with all optimizations
./gradlew assembleDebug \
    --build-cache \
    --configuration-cache \
    --parallel \
    -x lint \
    -x test \
    --daemon

echo ""
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Build failed!"
    echo "💡 Try: ./gradlew clean assembleDebug"
fi