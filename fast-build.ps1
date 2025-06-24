#!/usr/bin/env pwsh
# Cafezinho Fast Build Script for PowerShell

param(
    [string]$BuildType = "debug",
    [switch]$Install,
    [switch]$Continuous,
    [switch]$Clean,
    [switch]$Scan
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "        CAFEZINHO FAST BUILD" -ForegroundColor Cyan  
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set environment variables
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "[INFO] Using Java: $env:JAVA_HOME" -ForegroundColor Green
Write-Host "[INFO] Build Type: $BuildType" -ForegroundColor Green

# Build command base
$gradleTask = if ($Install) { "installDebug" } else { "assembleDebug" }
if ($BuildType -eq "release") { 
    $gradleTask = if ($Install) { "installRelease" } else { "assembleRelease" }
}

# Performance flags
$performanceFlags = @(
    "--parallel"
    "--configure-on-demand"  
    "--build-cache"
)

# Skip heavy tasks for faster builds
$skipTasks = @(
    "-x", "lint"
    "-x", "test"
    "-x", "lintDebug"
    "-x", "testDebugUnitTest"
    "-x", "lintAnalyzeDebug"
    "-x", "lintReportDebug"
)

# Additional flags
$additionalFlags = @()
if ($Clean) { $additionalFlags += "--recompile-scripts" }
if ($Scan) { $additionalFlags += "--scan" }
if ($Continuous) { $additionalFlags += "--continuous" }

# Build full command
$command = @(".\gradlew", $gradleTask) + $performanceFlags + $skipTasks + $additionalFlags

Write-Host ""
Write-Host "[BUILD] Command: $($command -join ' ')" -ForegroundColor Yellow
Write-Host ""

# Execute build
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()

try {
    & $command[0] $command[1..($command.Length-1)]
    $exitCode = $LASTEXITCODE
} catch {
    Write-Host "Error executing command: $_" -ForegroundColor Red
    exit 1
}

$stopwatch.Stop()
$elapsed = $stopwatch.Elapsed

if ($exitCode -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "    BUILD SUCCESS!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "Build time: $($elapsed.ToString('mm\:ss\.fff'))" -ForegroundColor Green
    Write-Host ""
    
    if (-not $Install -and -not $Continuous) {
        Write-Host "APK location: app\build\outputs\apk\debug\" -ForegroundColor Cyan
        $apkFiles = Get-ChildItem -Path "app\build\outputs\apk\debug\*.apk" -ErrorAction SilentlyContinue
        if ($apkFiles) {
            foreach ($apk in $apkFiles) {
                $sizeKB = [math]::Round($apk.Length / 1KB, 2)
                Write-Host "   $($apk.Name) ($sizeKB KB)" -ForegroundColor White
            }
        }
    }
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "       BUILD FAILED!" -ForegroundColor Red  
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "Failed after: $($elapsed.ToString('mm\:ss\.fff'))" -ForegroundColor Red
    exit $exitCode
}

Write-Host ""
Write-Host "Tips:" -ForegroundColor Cyan
Write-Host "   Continuous build: .\fast-build.ps1 -Continuous" -ForegroundColor Gray
Write-Host "   Install directly: .\fast-build.ps1 -Install" -ForegroundColor Gray  
Write-Host "   Build scan: .\fast-build.ps1 -Scan" -ForegroundColor Gray
Write-Host "   Clean build: .\fast-build.ps1 -Clean" -ForegroundColor Gray 