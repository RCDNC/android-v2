package com.rcdnc.cafezinho.features.swipe.di

import com.rcdnc.cafezinho.core.network.ApiConfig
import com.rcdnc.cafezinho.features.swipe.data.remote.SwipeApiService
import com.rcdnc.cafezinho.features.swipe.data.repository.SwipeRepositoryImpl
import com.rcdnc.cafezinho.features.swipe.domain.repository.SwipeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependência do Swipe/Descobrir
 * Seguindo o padrão implementado nos outros módulos
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SwipeModule {
    
    @Binds
    @Singleton
    abstract fun bindSwipeRepository(
        swipeRepositoryImpl: SwipeRepositoryImpl
    ): SwipeRepository
    
    companion object {
        
        @Provides
        @Singleton
        fun provideSwipeApiService(
            apiConfig: ApiConfig
        ): SwipeApiService {
            // TODO: Integrar com sistema de autenticação real
            val authInterceptor = ApiConfig.AuthInterceptor { 
                // Temporariamente null, integrar com AuthRepository quando disponível
                null 
            }
            
            val okHttpClient = apiConfig.createOkHttpClient(authInterceptor)
            val retrofit = apiConfig.createRetrofit(okHttpClient)
            
            return retrofit.create(SwipeApiService::class.java)
        }
    }
}