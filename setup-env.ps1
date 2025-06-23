# Script para configurar o ambiente de desenvolvimento Android no Windows
Write-Host "🚀 Configurando ambiente Android..." -ForegroundColor Green

# Configurar JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
Write-Host "✅ JAVA_HOME configurado: $env:JAVA_HOME" -ForegroundColor Yellow

# Configurar PATH
$env:PATH = $env:PATH + ";C:\Program Files\Android\Android Studio\jbr\bin;C:\android\platform-tools"
Write-Host "✅ PATH atualizado com Java e ADB" -ForegroundColor Yellow

# Verificar Java
Write-Host "☕ Versão do Java:" -ForegroundColor Cyan
java -version

# Verificar ADB
Write-Host "`n🔧 Versão do ADB:" -ForegroundColor Cyan
adb version

# Verificar dispositivos conectados
Write-Host "`n📱 Dispositivos conectados:" -ForegroundColor Cyan
adb devices

Write-Host "`n✨ Ambiente configurado com sucesso!" -ForegroundColor Green
Write-Host "Para fazer o build do projeto, execute: .\gradlew assembleDebug" -ForegroundColor White 