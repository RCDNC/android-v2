@echo off
echo ========================================
echo        CAFEZINHO FAST BUILD
echo ========================================
echo.

REM Set environment variables for this session
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set PATH=%JAVA_HOME%\bin;%PATH%

echo [INFO] Using Java: %JAVA_HOME%
echo [INFO] Starting optimized build...
echo.

REM Fast debug build with optimizations
echo [BUILD] Running assembleDebug with performance flags...
.\gradlew assembleDebug ^
  --parallel ^
  --configure-on-demand ^
  --build-cache ^
  -x lint ^
  -x test ^
  -x jacocoTestReport ^
  -x lintVitalAnalyzeRelease ^
  -x checkReleaseAarMetadata

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo    BUILD SUCCESS! APK GENERATED
    echo ========================================
    echo APK location: app\build\outputs\apk\debug\
    dir /b app\build\outputs\apk\debug\*.apk
) else (
    echo.
    echo ========================================
    echo       BUILD FAILED!
    echo ========================================
    echo Check the error messages above.
    exit /b %ERRORLEVEL%
)

echo.
echo [TIP] Para continuous build: .\gradlew assembleDebug --continuous
echo [TIP] Para instalar direto: .\gradlew installDebug
echo. 