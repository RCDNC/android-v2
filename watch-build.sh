#!/bin/bash

echo "👀 Continuous Build - Auto-rebuild on file changes"
echo "================================================="

echo "📱 Platform: $(uname -s) $(if [ -n "${WSL_DISTRO_NAME}" ]; then echo "(WSL: ${WSL_DISTRO_NAME})"; fi)"
echo "🔧 Watching for changes... (Press Ctrl+C to stop)"

# Continuous build
./gradlew installDebug \
    --continuous \
    --build-cache \
    --configuration-cache \
    --parallel \
    -x lint \
    -x test \
    --daemon