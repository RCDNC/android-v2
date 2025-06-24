#!/bin/bash

echo "📱 Hybrid Install Script - WSL + Windows Compatible"
echo "=================================================="

echo "🔍 Checking ADB connectivity..."

# Check if devices are connected
DEVICES=$(adb devices | grep -v "List of devices" | grep -v "^$" | wc -l)

if [ $DEVICES -gt 0 ]; then
    echo "✅ Device detected! Installing directly..."
    ./install-fast.sh
else
    echo "❌ No devices detected in WSL"
    echo ""
    echo "🎯 SOLUTIONS:"
    echo "1. 📶 WiFi ADB (Recommended):"
    echo "   - Windows: adb tcpip 5555"
    echo "   - Windows: adb connect <PHONE_IP>:5555"
    echo "   - WSL: adb connect <PHONE_IP>:5555"
    echo ""
    echo "2. 🔧 Manual Install:"
    echo "   - WSL: ./build-fast.sh"
    echo "   - Windows: adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "3. 🌐 Try ADB over network:"
    echo "   - Enter phone IP (or 'skip'): "
    
    read -r PHONE_IP
    
    if [ "$PHONE_IP" != "skip" ] && [ -n "$PHONE_IP" ]; then
        echo "🔗 Trying to connect to $PHONE_IP:5555..."
        adb connect "$PHONE_IP:5555"
        
        # Check again
        DEVICES=$(adb devices | grep -v "List of devices" | grep -v "^$" | wc -l)
        if [ $DEVICES -gt 0 ]; then
            echo "✅ Connected! Installing..."
            ./install-fast.sh
        else
            echo "❌ Connection failed. Use manual install."
        fi
    else
        echo "📱 Building APK for manual install..."
        ./build-fast.sh
        echo ""
        echo "📍 APK location: app/build/outputs/apk/debug/app-debug.apk"
        echo "🔧 Windows install: adb install app/build/outputs/apk/debug/app-debug.apk"
    fi
fi