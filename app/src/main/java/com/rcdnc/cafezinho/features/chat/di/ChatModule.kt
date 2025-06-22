package com.rcdnc.cafezinho.features.chat.di

import com.rcdnc.cafezinho.core.network.ApiConfig
import com.rcdnc.cafezinho.features.chat.data.remote.ChatApiService
import com.rcdnc.cafezinho.features.chat.data.repository.ChatRepositoryImpl
import com.rcdnc.cafezinho.features.chat.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependência do Chat
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {
    
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
    
    companion object {
        
        @Provides
        @Singleton
        fun provideChatApiService(
            apiConfig: ApiConfig
        ): ChatApiService {
            // TODO: Integrar com sistema de autenticação real
            val authInterceptor = ApiConfig.AuthInterceptor { 
                // Temporariamente null, integrar com AuthRepository quando disponível
                null 
            }
            
            val okHttpClient = apiConfig.createOkHttpClient(authInterceptor)
            val retrofit = apiConfig.createRetrofit(okHttpClient)
            
            return retrofit.create(ChatApiService::class.java)
        }
    }
}