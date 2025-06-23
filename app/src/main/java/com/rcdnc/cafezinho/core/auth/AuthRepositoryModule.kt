package com.rcdnc.cafezinho.core.auth

import com.rcdnc.cafezinho.features.auth.data.repository.SimpleAuthRepositoryImpl
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para AuthRepository
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthRepositoryModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        authManager: AuthManager
    ): AuthRepository {
        return SimpleAuthRepositoryImpl(authManager)
    }
} 