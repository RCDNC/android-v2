@echo off
echo Installing Cafezinho APK...
echo ============================

if not exist "app-debug.apk" (
    echo APK not found! Run copy-apk-to-windows.sh in WSL first.
    pause
    exit /b 1
)

echo Checking connected devices...
adb devices

echo.
echo Installing APK (with auto-uninstall)...

REM Always uninstall first to avoid signature conflicts
echo Uninstalling previous version...
adb uninstall com.rcdnc.cafezinho.debug

echo Installing fresh APK...
adb install app-debug.apk

if %errorlevel% == 0 (
    echo.
    echo Installation successful!
    goto START_APP
) else (
    echo.
    echo Installation failed!
    echo Make sure device is connected and USB debugging is enabled
    goto END
)

:START_APP
echo Starting app...
adb shell am start -n com.rcdnc.cafezinho.debug/com.rcdnc.cafezinho.MainActivity

:END
echo.
pause
