@echo off
echo 📱 Descobrindo IP do celular...
echo ================================

echo.
echo 🔍 Verificando dispositivos ADB conectados:
adb devices

echo.
echo 📶 Tentando descobrir IP automaticamente:
echo (Certifique-se que o celular está conectado via USB)

for /f "tokens=1" %%i in ('adb devices ^| findstr /v "List" ^| findstr /v "^$"') do (
    echo.
    echo 🔍 Dispositivo encontrado: %%i
    echo 📱 Obtendo IP do dispositivo...
    adb -s %%i shell ip route | findstr wlan
    adb -s %%i shell ifconfig wlan0 | findstr "inet addr"
)

echo.
echo 💡 Se não funcionou, tente manualmente:
echo    1. Configurações → Wi-Fi → (sua rede) → Detalhes
echo    2. Procure por "Endereço IP"

pause