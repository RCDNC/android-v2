# 🐛 [DOCUMENTAÇÃO] Configuração do Ambiente Android no Windows - Problemas e Soluções

## 📝 **Descrição**

Este issue documenta **todos os problemas encontrados** e **soluções implementadas** durante a configuração do ambiente de desenvolvimento Android no Windows para o projeto `android-v2`.

## ❌ **Problema Principal**

### **Erro Java Path Linux/Unix**
```
Value '/usr/lib/jvm/java-21-openjdk-amd64' given for org.gradle.java.home Gradle property is invalid (Java home supplied is invalid)
```

**Causa**: Path do Java hardcoded para sistemas Unix, incompatível com Windows.

## 🔧 **Soluções Implementadas**

### 1. **Configuração JAVA_HOME Windows**
```powershell
# PowerShell - Configuração permanente
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Android\Android Studio\jbr", "User")
```

### 2. **Atualização de Versões (Compatibilidade)**
- **AGP**: 8.10.1 → 8.7.3
- **Gradle**: 8.0 → 8.9
- **Kotlin**: 1.9.10 → 1.9.25
- **Hilt**: 2.48 → 2.49
- **Build Tools**: 33.0.1 → 35.0.0
- **Compile SDK**: 34 → 35

### 3. **Fix KAPT + Hilt Dependency Injection**
**Problema**: `AuthRepository cannot be provided without an @Provides-annotated method`

**Causa**: KAPT não processa múltiplos `@Provides` em Kotlin `object`

**Solução**: Separados em módulos distintos:
- `AuthManagerModule.kt`
- `AuthRepositoryModule.kt`

### 4. **Build Tools Corruption Fix**
```gradle
android {
    buildToolsVersion "35.0.0"
    compileSdk 35
}
```

### 5. **Android SDK Path Windows**
```properties
# local.properties
sdk.dir=C:\\Users\\[USERNAME]\\AppData\\Local\\Android\\Sdk
```

## 📁 **Arquivos Criados/Modificados**

### **Novos Arquivos**
- [x] `setup-env.ps1` - Script automático de configuração
- [x] `AuthManagerModule.kt` - Módulo separado para AuthManager
- [x] `AuthRepositoryModule.kt` - Módulo separado para AuthRepository
- [x] `ANDROID_SETUP_WINDOWS_GUIDE.md` - Guia completo

### **Arquivos Atualizados**
- [x] `build.gradle` (Project level) - AGP 8.7.3
- [x] `gradle-wrapper.properties` - Gradle 8.9
- [x] `app/build.gradle` - Kotlin 1.9.25, Hilt 2.49
- [x] `gradle.properties` - Build Tools 35.0.0
- [x] `local.properties` - SDK path Windows

### **Arquivos Removidos**
- [x] `AuthModule.kt` - Substituído pelos módulos separados
- [x] `FirebaseMessagingService.kt` - Temporariamente removido (falta config)

## 🚀 **Resultados**

✅ **Build Success**: APK gerado com sucesso (17MB)  
✅ **Installation Success**: App instalado em 16s  
✅ **Hilt DI**: Dependency injection funcionando  
✅ **Java 21**: Compatibilidade completa  
✅ **Environment**: Totalmente configurado para Windows  

## 📊 **Ambiente Testado**

- **OS**: Windows 10 (win32 10.0.22631)
- **Shell**: PowerShell
- **IDE**: Android Studio com OpenJDK 21.0.5
- **Dispositivo**: Emulador Pixel 8

## 📚 **Documentação Criada**

Todo o processo foi documentado em:
- `ANDROID_SETUP_WINDOWS_GUIDE.md` - Guia completo step-by-step
- `setup-env.ps1` - Script de configuração automática

## 🎯 **Para Novos Desenvolvedores**

Este issue serve como **referência completa** para qualquer desenvolvedor que precise configurar o ambiente Android no Windows. Todas as soluções estão testadas e funcionando.

## 🔗 **Referências**

- [Android Gradle Plugin Compatibility](https://developer.android.com/studio/releases/gradle-plugin)
- [Gradle Java Compatibility](https://docs.gradle.org/current/userguide/compatibility.html)
- [Hilt Migration Guide](https://developer.android.com/training/dependency-injection/hilt-android)

---

**Labels**: `documentation`, `environment-setup`, `windows`, `android`, `gradle`, `hilt`, `java-21`  
**Priority**: `High` (Blocker para desenvolvimento no Windows)  
**Status**: `Resolved` ✅ 

# [DOCUMENTAÇÃO] Configuração Java/JDK Windows - Resolução MockK e Dependências

## 📋 Resumo
Documentação completa da resolução de problemas de configuração Java/JDK no Windows que causavam falhas na resolução de dependências MockK e compilação do projeto Android v2.

## 🚨 Problemas Identificados

### 1. Erro de Dependência MockK
```
Failed to resolve: io.mockk:mockk:1.13.15
Show in Project Structure dialog
Affected Modules: app
```

### 2. Erro de Java Home Inválido  
```
Value '/usr/lib/jvm/java-21-openjdk-amd64' given for org.gradle.java.home Gradle property is invalid (Java home supplied is invalid)
```

### 3. Java não encontrado no PATH
```
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
```

## ✅ Soluções Implementadas

### Commits relacionados:
- **Correção principal:** [1741262](https://github.com/RCDNC/android-v2/commit/1741262)
- **Documentação:** [def542b](https://github.com/RCDNC/android-v2/commit/def542b)

### Mudanças no gradle.properties:
```diff
- # Use WSL Linux JDK (Java 21 for full compatibility)
- org.gradle.java.home=/usr/lib/jvm/java-21-openjdk-amd64
- org.gradle.java.installations.auto-detect=false
+ # Use Windows JDK from Android Studio (Java 21 for full compatibility)  
+ org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
+ org.gradle.java.installations.auto-detect=true
```

### Configuração JAVA_HOME no Windows:
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

## 📚 Documentação Criada

Foi criado o arquivo `SETUP_JAVA_WINDOWS.md` contendo:
- ✅ Guia passo-a-passo de configuração
- ✅ Checklist para novos desenvolvedores  
- ✅ Comandos de verificação
- ✅ Resultados esperados
- ✅ Troubleshooting comum

## 🎯 Benefícios para a Equipe

1. **Resolução Documentada:** Outros desenvolvedores podem seguir os mesmos passos
2. **Setup Padronizado:** Evita problemas similares no futuro
3. **Onboarding Melhorado:** Novos devs têm guia claro de configuração
4. **Troubleshooting:** Documenta erros comuns e suas soluções

## 🔗 Arquivos Relacionados

- 📄 `gradle.properties` - Configuração corrigida
- 📄 `SETUP_JAVA_WINDOWS.md` - Guia completo de setup
- 📄 `build.gradle` - Dependências MockK funcionando

## ✅ Status Final

- [x] MockK 1.13.15 resolvendo corretamente
- [x] Java 21 configurado e funcionando  
- [x] Gradle 8.9 executando normalmente
- [x] Documentação completa criada
- [x] Setup validado no Windows

## 🎬 Para Criar esta Issue:

1. Acesse: https://github.com/RCDNC/android-v2/issues/new
2. Título: `[DOCUMENTAÇÃO] Configuração Java/JDK Windows - Resolução MockK e Dependências`
3. Copie este conteúdo como descrição
4. Adicione labels: `documentation`, `setup`, `resolved`, `java`, `windows`
5. Milestone: Próxima release

---

**Criado em:** Janeiro 2025  
**Resolvido por:** Equipe de desenvolvimento  
**Plataforma:** Windows 11  
**Ambiente:** Android Studio + Gradle 8.9 + Java 21 