# 🚀 Cafezinho Android v2

**Clean Architecture + Jetpack Compose + MVI**

## 📁 Project Structure

```
📦 com.rcdnc.cafezinho/
 ┣━ 📂 features/                     # Feature-based modules
 ┃   ┣━ 📂 auth/                     # Authentication feature
 ┃   ┃   ┣━ 📂 domain/
 ┃   ┃   ┃   ┣━ model/
 ┃   ┃   ┃   ┃   └━ User.kt
 ┃   ┃   ┃   ┣━ repository/
 ┃   ┃   ┃   ┃   └━ AuthRepository.kt
 ┃   ┃   ┃   └━ usecase/
 ┃   ┃   ┃       ┣━ LoginUseCase.kt
 ┃   ┃   ┃       └━ SignupUseCase.kt
 ┃   ┃   ┣━ 📂 data/
 ┃   ┃   ┃   └━ repository/
 ┃   ┃   ┃       └━ AuthRepositoryImpl.kt
 ┃   ┃   ┣━ 📂 presentation/
 ┃   ┃   ┃   ┣━ screen/
 ┃   ┃   ┃   ┣━ component/
 ┃   ┃   ┃   └━ navigation/
 ┃   ┃   └━ 📂 mvi/
 ┃   ┃       ┣━ AuthIntent.kt
 ┃   ┃       ┣━ AuthState.kt
 ┃   ┃       └━ AuthViewModel.kt
 ┃   ┣━ 📂 swipe/                    # Core dating feature
 ┃   ┣━ 📂 matches/                  # Matches & "Who liked you"
 ┃   ┣━ 📂 chat/                     # Chat & messaging
 ┃   ┣━ 📂 profile/                  # User profile management
 ┃   └━ 📂 settings/                 # App settings
 ┣━ 📂 di/                           # Dependency Injection
 ┣━ 📂 navigation/                   # App Navigation
 ┣━ 📂 ui/                          # Design System
 ┗━ 📂 common/                       # Shared utilities
```

## 🎯 Architecture Principles

### Clean Architecture Layers
- **Domain Layer**: Business logic, models, use cases
- **Data Layer**: Repository implementations, API services
- **Presentation Layer**: UI, ViewModels, Compose screens

### MVI Pattern
- **Intent**: User actions and system events
- **State**: Immutable UI state representation
- **ViewModel**: State management and business logic coordination

### Technologies
- **Jetpack Compose** - Modern UI toolkit
- **Hilt** - Dependency injection
- **Navigation Compose** - Type-safe navigation
- **Coroutines + Flow** - Asynchronous programming
- **Retrofit** - Network layer
- **Room** - Local database

## 🚀 Getting Started

### Prerequisites
- Android Studio Giraffe+
- JDK 17+
- Android SDK 34+

### Setup
```bash
git clone https://github.com/RCDNC/android-v2.git
cd android-v2
./gradlew assembleDebug
```

## 📋 Current Status

✅ **Completed**
- Clean Architecture foundation
- Navigation system with Compose
- MVI pattern implementation
- DI with Hilt setup

🔄 **In Progress**
- Feature implementations (auth, swipe, chat, matches, profile, settings)
- UI components with Material Design 3
- Comprehensive testing suite

⏳ **Planned**
- CI/CD pipeline
- Performance optimizations
- Accessibility improvements

## 🏆 Migration Strategy

This v2 represents a complete rewrite following modern Android development best practices, migrating from the legacy XML-based architecture to a Clean Architecture + Compose approach.

---

**Generated with [Claude Code](https://claude.ai/code)**