#!/bin/bash
# Setup Android Development Environment for WSL

# Set Android SDK path
export ANDROID_HOME=/home/ubuntu/docker/cafezinho/android-v2/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME

# Set Java path  
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Add to PATH
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$PATH
export PATH=$ANDROID_HOME/platform-tools:$PATH
export PATH=$JAVA_HOME/bin:$PATH

echo "✅ Android Development Environment Setup Complete!"
echo "ANDROID_HOME: $ANDROID_HOME"
echo "JAVA_HOME: $JAVA_HOME"
echo ""
echo "Run: source setup-env.sh before using Gradle"