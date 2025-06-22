package com.rcdnc.cafezinho.features.profile.di

import com.rcdnc.cafezinho.core.network.ApiConfig
import com.rcdnc.cafezinho.features.profile.data.remote.ProfileApiService
import com.rcdnc.cafezinho.features.profile.data.repository.ProfileRepositoryImpl
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependência do Profile
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {
    
    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
    
    companion object {
        
        @Provides
        @Singleton
        fun provideProfileApiService(
            apiConfig: ApiConfig
        ): ProfileApiService {
            // TODO: Integrar com sistema de autenticação real
            val authInterceptor = ApiConfig.AuthInterceptor { 
                // Temporariamente null, integrar com AuthRepository quando disponível
                null 
            }
            
            val okHttpClient = apiConfig.createOkHttpClient(authInterceptor)
            val retrofit = apiConfig.createRetrofit(okHttpClient)
            
            return retrofit.create(ProfileApiService::class.java)
        }
    }
}