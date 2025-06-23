# Configuração Java/JDK no Windows - Guia de Resolução

Este documento documenta a resolução dos problemas de configuração Java/JDK no Windows que causavam falhas na resolução de dependências MockK e compilação do projeto.

## 🚨 Problemas Identificados

### 1. Erro de Dependência MockK
```
Failed to resolve: io.mockk:mockk:1.13.15
```

### 2. Erro de Java Home Inválido
```
Value '/usr/lib/jvm/java-21-openjdk-amd64' given for org.gradle.java.home Gradle property is invalid (Java home supplied is invalid)
```

### 3. Erro de Java não encontrado
```
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
```

## 🔧 Soluções Implementadas

### 1. Configuração do gradle.properties

O arquivo `gradle.properties` foi atualizado para usar o JDK do Android Studio no Windows:

```properties
# Java toolchain configuration
# org.gradle.java.home will be auto-detected or set via JAVA_HOME
# Set Java toolchain version  
org.gradle.java.installations.auto-detect=true
```

**Antes (problemático):**
```properties
# Use WSL Linux JDK (Java 21 for full compatibility)
org.gradle.java.home=/usr/lib/jvm/java-21-openjdk-amd64
# Set Java toolchain version  
org.gradle.java.installations.auto-detect=false
```

### 2. Configuração JAVA_HOME no Windows

Para resolver permanentemente, configure as variáveis de ambiente do Windows:

#### Via PowerShell (temporário para sessão atual):
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

#### Via Configurações do Sistema (permanente):
1. Abra **Configurações do Sistema** → **Variáveis de Ambiente**
2. Adicione nova variável de sistema:
   - **Nome:** `JAVA_HOME`
   - **Valor:** `C:\Program Files\Android\Android Studio\jbr`
3. Edite a variável `PATH` e adicione: `%JAVA_HOME%\bin`

### 3. Verificação da Configuração

Após a configuração, verifique se tudo está funcionando:

```powershell
# Verificar versão do Java
java -version

# Verificar JAVA_HOME
echo $env:JAVA_HOME

# Verificar versão do Gradle
./gradlew --version

# Testar resolução de dependências MockK
./gradlew app:dependencies --configuration testImplementation | Select-String "mockk"
```

## ✅ Resultados Esperados

Após aplicar as configurações:

1. **Java 21 funcionando:**
```
openjdk version "21.0.6" 2025-01-21
OpenJDK Runtime Environment (build 21.0.6+-13368085-b895.109)
OpenJDK 64-Bit Server VM (build 21.0.6+-13368085-b895.109, mixed mode)
```

2. **Gradle funcionando:**
```
Gradle 8.9
Launcher JVM:  21.0.6 (JetBrains s.r.o. 21.0.6+-13368085-b895.109)
Daemon JVM:    'C:\Program Files\Android\Android Studio\jbr' (from org.gradle.java.home)
```

3. **MockK dependências resolvidas:**
```
+--- io.mockk:mockk:1.13.15 (n)
+--- io.mockk:mockk-android:1.13.15 (n)
```

## 📋 Checklist para Novos Desenvolvedores

- [ ] Android Studio instalado com JDK 21
- [ ] JAVA_HOME configurado apontando para `C:\Program Files\Android\Android Studio\jbr`
- [ ] PATH atualizado incluindo `%JAVA_HOME%\bin`
- [ ] Comando `java -version` funcionando
- [ ] Comando `./gradlew --version` funcionando
- [ ] Build do projeto executando sem erros de dependências

## 🔗 Links Relacionados

- **Commit de correção:** [1741262](https://github.com/RCDNC/android-v2/commit/1741262)
- **Gradle JVM Configuration:** https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
- **Android Studio JDK:** https://developer.android.com/studio/intro/studio-config#jdk

## 📝 Notas Adicionais

- Esta configuração resolve especificamente problemas no Windows
- Para Linux/macOS, a configuração pode ser diferente
- Certifique-se de que o Android Studio está atualizado para garantir compatibilidade com Java 21
- O auto-detect de instalações Java está habilitado para maior flexibilidade

---

**Data de criação:** Janeiro 2025  
**Última atualização:** Janeiro 2025  
**Responsável:** Equipe de desenvolvimento Android v2 