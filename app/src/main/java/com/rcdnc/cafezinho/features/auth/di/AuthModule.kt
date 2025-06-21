package com.rcdnc.cafezinho.features.auth.di

import com.rcdnc.cafezinho.features.auth.data.repository.SimplifiedAuthRepositoryImpl
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for authentication dependencies
 * Provides repository implementations and other auth-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: SimplifiedAuthRepositoryImpl
    ): AuthRepository
}

/**
 * Additional module for providing concrete implementations
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthProvidersModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): com.google.firebase.auth.FirebaseAuth = com.google.firebase.auth.Firebase.auth
}