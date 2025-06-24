#!/bin/bash

echo "📱 Setup WiFi ADB - Automatic IP Discovery"
echo "==========================================="

echo "📋 INSTRUÇÕES:"
echo "1. Conecte o celular via USB no Windows"
echo "2. Execute este comando no Windows:"
echo "   adb devices"
echo "3. Se aparecer o dispositivo, execute:"
echo "   adb shell ip route | findstr wlan"
echo "4. Copie o IP que aparecer e cole aqui"
echo ""

echo "🔍 Ou use o script discover-phone-ip.bat no Windows"
echo ""

echo "📱 Digite o IP do seu celular (ex: 192.168.1.100):"
read -r PHONE_IP

if [ -z "$PHONE_IP" ]; then
    echo "❌ IP não informado. Saindo..."
    exit 1
fi

echo ""
echo "🔗 Configurando ADB WiFi..."
echo "💡 Execute estes comandos no Windows primeiro:"
echo ""
echo "   adb tcpip 5555"
echo "   adb connect $PHONE_IP:5555"
echo ""
echo "⏳ Aguardando 10 segundos para você executar no Windows..."
echo "   (Pressione Ctrl+C se não quiser aguardar)"

for i in {10..1}; do
    echo -ne "\r⏱️  $i segundos restantes..."
    sleep 1
done

echo ""
echo ""
echo "🔗 Tentando conectar no WSL..."
adb connect "$PHONE_IP:5555"

echo ""
echo "🔍 Verificando dispositivos conectados:"
adb devices

DEVICES=$(adb devices | grep -v "List of devices" | grep -v "^$" | wc -l)

if [ $DEVICES -gt 0 ]; then
    echo ""
    echo "✅ Sucesso! Dispositivo conectado via WiFi"
    echo "🚀 Executando instalação..."
    ./install-fast.sh
else
    echo ""
    echo "❌ Não conseguiu conectar"
    echo "💡 Tente executar no Windows:"
    echo "   adb tcpip 5555"
    echo "   adb connect $PHONE_IP:5555"
    echo "   Depois execute aqui: adb connect $PHONE_IP:5555"
fi