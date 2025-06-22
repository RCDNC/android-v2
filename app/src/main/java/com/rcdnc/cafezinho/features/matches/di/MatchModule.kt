package com.rcdnc.cafezinho.features.matches.di

import com.rcdnc.cafezinho.core.network.ApiConfig
import com.rcdnc.cafezinho.features.matches.data.remote.MatchApiService
import com.rcdnc.cafezinho.features.matches.data.repository.MatchRepositoryImpl
import com.rcdnc.cafezinho.features.matches.domain.repository.MatchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependência do Matches
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MatchModule {
    
    @Binds
    @Singleton
    abstract fun bindMatchRepository(
        matchRepositoryImpl: MatchRepositoryImpl
    ): MatchRepository
    
    companion object {
        
        @Provides
        @Singleton
        fun provideMatchApiService(
            apiConfig: ApiConfig
        ): MatchApiService {
            // TODO: Integrar com sistema de autenticação real
            val authInterceptor = ApiConfig.AuthInterceptor { 
                // Temporariamente null, integrar com AuthRepository quando disponível
                null 
            }
            
            val okHttpClient = apiConfig.createOkHttpClient(authInterceptor)
            val retrofit = apiConfig.createRetrofit(okHttpClient)
            
            return retrofit.create(MatchApiService::class.java)
        }
    }
}