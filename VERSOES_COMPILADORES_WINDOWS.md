# 📋 Relatório Completo - Versões dos Compiladores Windows

**Data**: 23/06/2025  
**Computador**: Windows 10 (10.0.22631)  
**Usuário**: DELL-19  

## ☕ **Java (OpenJDK)**
```
Versão: OpenJDK 21.0.5 (2024-10-15)
Runtime: OpenJDK Runtime Environment (build 21.0.5+-13047016-b750.29)
VM: OpenJDK 64-Bit Server VM (build 21.0.5+-13047016-b750.29, mixed mode)
Fornecedor: JetBrains s.r.o. (Android Studio JBR)
Caminho: C:\Program Files\Android\Android Studio\jbr
```

## 🐘 **Gradle**
```
Versão: Gradle 8.9
Build time: 2024-07-11 14:37:41 UTC
Revision: d536ef36a19186ccc596d8817123e5445f30fef8
Kotlin interno: 1.9.23
Groovy: 3.0.21
Ant: Apache Ant(TM) version 1.10.13 (January 4 2023)
Launcher JVM: 21.0.5 (JetBrains s.r.o.)
Daemon JVM: C:\Program Files\Android\Android Studio\jbr
OS: Windows 11 10.0 amd64
```

## 🤖 **Android SDK**
```
SDK Path: C:\Users\DELL-19\AppData\Local\Android\Sdk
ADB Version: 1.0.41 (Version 35.0.2-12147458)
Platform Tools: 35.0.2

Build Tools Instalados:
├── 33.0.1
├── 34.0.0  
├── 35.0.0 ⭐ (usado no projeto)
├── 35.0.1
└── 36.0.0

Android Platforms (APIs):
├── android-34 (API 34)
└── android-35 (API 35) ⭐ (usado no projeto)
```

## 🏗️ **Android Gradle Plugin (AGP)**
```
Versão: 8.7.3 ⭐ (definido no projeto)
Compatível com: Gradle 8.9
```

## 🎯 **Kotlin**
```
Versão do Projeto: 1.9.25 ⭐
Versão no Gradle: 1.9.23
Multiplatform: 1.9.25
Android Plugin: 1.9.25
Serialization: 1.9.25
```

## 💉 **Hilt (Dependency Injection)**
```
Versão: 2.49 ⭐
Hilt Navigation Compose: 1.2.0
```

## 🎨 **Jetpack Compose**
```
Compose BOM: 2025.01.01 ⭐
Kotlin Compiler Extension: 1.5.15
Navigation Compose: 2.8.5
Activity Compose: 1.9.3
Material3: 1.3.1 (via BOM)
```

## 📚 **Outras Bibliotecas Principais**
```
Coroutines: 1.8.1
Retrofit: 2.11.0
OkHttp: 4.12.0
Gson: 2.11.0
Coil (Image Loading): 2.7.0
Lifecycle: 2.8.7
Core KTX: 1.15.0
```

## ⚙️ **Configurações do Projeto**
```
Namespace: com.rcdnc.cafezinho
Application ID: com.rcdnc.cafezinho (debug: .debug)
Min SDK: 24
Target SDK: 35
Compile SDK: 35
Version Code: 1
Version Name: 2.0.0

Java Source/Target: VERSION_21
Kotlin JVM Target: 21
```

## 🔧 **Gradle Properties**
```
JVM Args: -Xmx4096m -Dfile.encoding=UTF-8 -XX:+UseG1GC
Gradle Daemon: true
Parallel Builds: true
Configuration Cache: false (disabled for Java 21)
Java Home: C:\\Program Files\\Android\\Android Studio\\jbr
Auto-detect Java: false
AndroidX: true
Kotlin Code Style: official
Non-transitive R Class: true
```

## 🎯 **Versões Recomendadas para Funcionamento**

### **Essenciais (devem ser iguais):**
- **Java**: OpenJDK 21.0.5+ (Android Studio JBR)
- **Gradle**: 8.9
- **AGP**: 8.7.3
- **Kotlin**: 1.9.25
- **Hilt**: 2.49

### **Android SDK (devem estar instalados):**
- **Build Tools**: 35.0.0+
- **Platform**: android-35 (API 35)
- **Platform Tools**: 35.0.0+

### **JAVA_HOME (crítico):**
```
Windows: C:\Program Files\Android\Android Studio\jbr
Linux/macOS: /Applications/Android Studio.app/Contents/jbr/Contents/Home
```

## ❌ **Problemas Conhecidos**

### **Se o outro computador usar:**
- Java 8/11 → **Incompatível** (precisa Java 21)
- Gradle < 8.5 → **Incompatível** com Java 21
- AGP < 8.7.0 → **Pode ter problemas** com Gradle 8.9
- Kotlin < 1.9.20 → **Incompatível** com Compose
- JAVA_HOME Linux → **Erro no Windows**

### **Comandos para Verificar no Outro PC:**
```bash
# Java
java -version

# Gradle  
./gradlew --version

# Android SDK
echo $ANDROID_HOME (Linux/macOS)
echo %ANDROID_HOME% (Windows CMD)
echo $env:ANDROID_HOME (Windows PowerShell)

# Build Tools
ls "$ANDROID_HOME/build-tools"

# Variáveis críticas
echo $JAVA_HOME
```

---

**💡 Dica**: Copie este arquivo para o outro computador e compare linha por linha. As diferenças mais prováveis estão em Java, JAVA_HOME e versões do Android SDK. 