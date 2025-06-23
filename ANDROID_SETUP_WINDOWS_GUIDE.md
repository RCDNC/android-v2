# 🔧 Guia Completo: Configuração do Ambiente Android no Windows

## 📋 Problemas Resolvidos e Soluções

Este documento registra todos os problemas encontrados durante a configuração do ambiente de desenvolvimento Android no Windows e suas respectivas soluções.

---

## ❌ **PROBLEMA PRINCIPAL: "Value '/usr/lib/jvm/java-21-openjdk-amd64' given for org.gradle.java.home Gradle property is invalid"**

### 🔍 **Causa Raiz**
- O projeto foi configurado originalmente em Linux/macOS
- O path do Java estava hardcoded para sistemas Unix no `gradle.properties` ou variáveis de ambiente
- Windows usa paths diferentes (`C:\Program Files\...` ao invés de `/usr/lib/...`)

### ✅ **Solução Implementada**

#### 1. **Configuração do JAVA_HOME correta para Windows**
```bash
# PowerShell - Configuração permanente
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Android\Android Studio\jbr", "User")
```

#### 2. **Atualização do PATH**
```bash
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
[Environment]::SetEnvironmentVariable("PATH", "$env:JAVA_HOME\bin;$env:PATH", "User")
```

#### 3. **Verificação da configuração**
```bash
java -version
# Output esperado: openjdk version "21.0.5"
```

---

## 🛠️ **PROBLEMAS SECUNDÁRIOS RESOLVIDOS**

### 1. **Build Tools Corruption**
**Erro**: `Installed Build Tools revision 33.0.1 is corrupted`

**Solução**:
```gradle
// build.gradle (Project level)
android {
    buildToolsVersion "35.0.0"
    compileSdk 35
}

// gradle.properties
android.buildToolsVersion=35.0.0
```

### 2. **AGP/Gradle Incompatibilidade**
**Erro**: `The project is using an incompatible version (AGP 8.10.1) of the Android Gradle plugin`

**Solução**:
```gradle
// build.gradle (Project level)
dependencies {
    classpath 'com.android.tools.build:gradle:8.7.3'
}

// gradle/wrapper/gradle-wrapper.properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip
```

### 3. **Java 21 + Gradle Incompatibilidade**
**Erro**: `Unsupported class file major version 65`

**Solução**: Upgrade Gradle 8.0 → 8.9 (suporta Java 21)

### 4. **KAPT + Kotlin Object Issue**
**Erro**: `AuthRepository cannot be provided without an @Provides-annotated method`

**Causa**: KAPT não processa múltiplos `@Provides` em Kotlin `object`

**Solução**: Separar em módulos diferentes
```kotlin
// AuthManagerModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AuthManagerModule {
    @Provides
    @Singleton
    fun provideAuthManager(@ApplicationContext context: Context): AuthManager {
        return AuthManager(context)
    }
}

// AuthRepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AuthRepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(authManager: AuthManager): AuthRepository {
        return SimpleAuthRepositoryImpl(authManager)
    }
}
```

### 5. **Android SDK Path no Windows**
```properties
# local.properties
sdk.dir=C:\\Users\\DELL-19\\AppData\\Local\\Android\\Sdk
```

---

## 🚀 **CONFIGURAÇÃO COMPLETA FINAL**

### **Versões Atualizadas**
- **AGP**: 8.7.3
- **Gradle**: 8.9
- **Kotlin**: 1.9.25
- **Hilt**: 2.49
- **Java**: OpenJDK 21.0.5
- **Build Tools**: 35.0.0
- **Compile SDK**: 35

### **Script de Configuração Automática**
```powershell
# setup-env.ps1 - Script criado para automação
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME = "C:\Users\DELL-19\AppData\Local\Android\Sdk"
$env:PATH = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\tools;$env:PATH"

Write-Host "✅ Ambiente configurado com sucesso!"
Write-Host "Java Home: $env:JAVA_HOME"
Write-Host "Android Home: $env:ANDROID_HOME"
```

---

## 📱 **INSTALAÇÃO E TESTE**

### **APK Build & Install**
```bash
# Limpar cache
.\gradlew clean

# Build APK
.\gradlew assembleDebug

# Instalar em dispositivo/emulador
.\gradlew installDebug
```

### **Localização do APK**
```
C:\cafezinho\android-v2\app\build\outputs\apk\debug\app-debug.apk
```

---

## 🔧 **COMANDOS DE TROUBLESHOOTING**

### **Cache Issues**
```bash
# Limpar cache Gradle
.\gradlew clean
.\gradlew --stop
Remove-Item -Recurse -Force ~/.gradle/caches

# Limpar cache KAPT/Hilt
Remove-Item -Recurse -Force .\app\build\generated\source\kapt
```

### **Verificar Devices**
```bash
# Listar dispositivos conectados
adb devices

# Listar emuladores disponíveis
& "$env:ANDROID_HOME\emulator\emulator.exe" -list-avds

# Iniciar emulador
& "$env:ANDROID_HOME\emulator\emulator.exe" -avd Pixel_8
```

---

## 📊 **RESULTADOS FINAIS**

✅ **Build Success**: APK gerado com sucesso (17MB)  
✅ **Installation Success**: App instalado em 16s  
✅ **Hilt DI**: Dependency injection funcionando  
✅ **Java 21**: Compatibilidade completa  
✅ **Modern Android**: API 35, AGP 8.7.3  

---

## 📚 **REFERÊNCIAS TÉCNICAS**

- [Android Gradle Plugin Release Notes](https://developer.android.com/studio/releases/gradle-plugin)
- [Gradle Compatibility Matrix](https://docs.gradle.org/current/userguide/compatibility.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [KAPT Migration Guide](https://kotlinlang.org/docs/kapt.html)

---

**Autor**: Assistant IA  
**Data**: Dezembro 2024  
**Repositório**: [RCDNC/android-v2](https://github.com/RCDNC/android-v2)  
**Ambiente**: Windows 10 (win32 10.0.22631) 