package com.rcdnc.cafezinho.core.auth

import android.content.Context
import com.rcdnc.cafezinho.features.auth.data.repository.SimpleAuthRepositoryImpl
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para autenticação
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    
    @Provides
    @Singleton
    fun provideAuthManager(
        @ApplicationContext context: Context
    ): AuthManager {
        return AuthManager(context)
    }
}