#!/bin/bash

echo "🔧 Testing Multi-Platform Gradle Detection"
echo "=========================================="

echo "📍 Current environment:"
echo "  - OS: $(uname -s)"
echo "  - WSL_DISTRO_NAME: ${WSL_DISTRO_NAME:-'not set'}"
echo "  - ANDROID_HOME: ${ANDROID_HOME:-'not set'}"
echo "  - ANDROID_SDK_ROOT: ${ANDROID_SDK_ROOT:-'not set'}"

echo ""
echo "🎯 Running Gradle platform detection..."
./gradlew setupLocalProperties

echo ""
echo "📱 Checking local.properties..."
cat local.properties

echo ""
echo "✅ Platform detection test complete!"