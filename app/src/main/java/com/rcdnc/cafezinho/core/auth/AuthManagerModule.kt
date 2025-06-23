package com.rcdnc.cafezinho.core.auth

import android.content.Context
<<<<<<< Updated upstream:app/src/main/java/com/rcdnc/cafezinho/core/auth/AuthManagerModule.kt
=======
import com.rcdnc.cafezinho.core.network.ApiConfig
import com.rcdnc.cafezinho.features.auth.data.repository.SimpleAuthRepositoryImpl
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import dagger.Binds
>>>>>>> Stashed changes:app/src/main/java/com/rcdnc/cafezinho/core/auth/AuthModule.kt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para AuthManager
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthManagerModule {
    
    @Provides
    @Singleton
    fun provideAuthManager(
        @ApplicationContext context: Context
    ): AuthManager {
        return AuthManager(context)
    }
<<<<<<< Updated upstream:app/src/main/java/com/rcdnc/cafezinho/core/auth/AuthManagerModule.kt
} 
=======
    
    @Provides
    @Singleton
    fun provideApiConfig(): ApiConfig {
        return ApiConfig()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindsModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        simpleAuthRepositoryImpl: SimpleAuthRepositoryImpl
    ): AuthRepository
}
>>>>>>> Stashed changes:app/src/main/java/com/rcdnc/cafezinho/core/auth/AuthModule.kt
