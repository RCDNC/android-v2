# 🚀 Guia de Performance para Builds do Cafezinho Android

## 📊 Resultados das Otimizações

### Antes (Build padrão)
- **Tempo:** ~4+ minutos
- **Comando:** `.\gradlew assembleDebug`
- **Tarefas executadas:** Todas (lint, testes, análises, etc.)

### Depois (Build otimizado)
- **Tempo:** ~4 minutos (primeira execução), subsequentes muito mais rápidas
- **Comando:** `.\fast-build.ps1`
- **Tarefas puladas:** lint, testes, análises desnecessárias
- **Cache ativo:** Build incrementais muito mais rápidos

## ⚡ Otimizações Implementadas

### 1. Gradle Properties (`gradle.properties`)

```properties
# =======================
# PERFORMANCE OPTIMIZATIONS
# =======================

# Memory and GC optimization
org.gradle.jvmargs=-Xmx6144m -Dfile.encoding=UTF-8 -XX:+UseG1GC -XX:MaxMetaspaceSize=1g -XX:+HeapDumpOnOutOfMemoryError

# Build performance
org.gradle.daemon=true                    # Keep daemon in memory
org.gradle.parallel=true                 # Parallel module compilation
org.gradle.configureondemand=true        # Skip unchanged projects
org.gradle.caching=true                  # Cache build outputs
org.gradle.workers.max=8                 # CPU cores (adjust for your system)

# Android optimizations
android.enableJetifier=false             # Faster if all libs use AndroidX
android.nonTransitiveRClass=true         # Smaller R classes

# Kotlin optimizations
kotlin.incremental=true                  # Incremental compilation
kotlin.experimental.tryK2=true           # Faster K2 compiler
```

### 2. Build Scripts Otimizados

#### PowerShell (`fast-build.ps1`)
```powershell
.\fast-build.ps1                # Build rápido
.\fast-build.ps1 -Install       # Build + instalação
.\fast-build.ps1 -Continuous    # Continuous build (hot reload)
.\fast-build.ps1 -Scan          # Build com análise detalhada
```

#### Batch (`fast-build.bat`)
```bat
fast-build.bat                  # Build rápido (Windows)
```

### 3. Flags de Performance Usadas

```bash
--parallel                      # Compilação paralela
--configure-on-demand           # Configuração sob demanda
--build-cache                   # Cache de build
-x lint                         # Pula análise de lint
-x test                         # Pula testes unitários
-x lintDebug                    # Pula lint debug
-x testDebugUnitTest            # Pula testes debug
-x lintAnalyzeDebug             # Pula análise lint debug
-x lintReportDebug              # Pula relatório lint debug
```

## 🎯 Comandos Essenciais

### Build Rápido (desenvolvimento)
```bash
.\gradlew assembleDebug --parallel --configure-on-demand --build-cache -x lint -x test
```

### Continuous Build (hot reload)
```bash
.\gradlew assembleDebug --continuous
```

### Instalar Direto no Dispositivo
```bash
.\gradlew installDebug
```

### Build com Análise Completa
```bash
.\gradlew build --scan
```

### Limpar Cache (quando necessário)
```bash
.\gradlew clean
```

## 📱 APK Gerado

- **Localização:** `app/build/outputs/apk/debug/app-debug.apk`
- **Tamanho:** ~16.6 MB
- **Compatibilidade:** Android API 24+ (Android 7.0+)

## 🔧 Ajustes por Sistema

### CPU Cores
Ajuste no `gradle.properties`:
```properties
org.gradle.workers.max=X  # X = número de cores do seu CPU
```

### Memória RAM
Para sistemas com menos RAM:
```properties
org.gradle.jvmargs=-Xmx4096m  # Reduza de 6144m para 4096m
```

Para sistemas com mais RAM:
```properties
org.gradle.jvmargs=-Xmx8192m  # Aumente para 8192m
```

## 📈 Próximos Passos para Melhorar Performance

1. **Modularização:** Separar features em módulos independentes
2. **KSP Migration:** Migrar completamente de KAPT para KSP
3. **R8/ProGuard:** Otimizar apenas quando necessário
4. **Baseline Profiles:** Para apps de produção
5. **Build Variants:** Criar variant de desenvolvimento sem otimizações pesadas

## 🚨 Troubleshooting

### Build Cache Corrompido
```bash
.\gradlew --stop
Remove-Item -Recurse -Force ~/.gradle/caches
.\gradlew assembleDebug
```

### Java Path Issues
```bash
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

### Gradle Daemon Issues
```bash
.\gradlew --stop
.\gradlew --status
```

## 💡 Dicas Extras

- **Evite `clean`** a menos que necessário - remove todo o cache
- **Use `installDebug`** para testar no dispositivo - mais eficiente
- **Continuous build** é ideal para desenvolvimento de UI
- **Build Scan** ajuda a identificar gargalos específicos
- **Monitore o cache** - builds subsequentes devem ser muito mais rápidos

---

*Última atualização: Janeiro 2025* 