package com.rcdnc.cafezinho.core.di

import android.content.Context
import com.rcdnc.cafezinho.core.auth.AuthManager
import com.rcdnc.cafezinho.core.network.ApiConfig
import com.rcdnc.cafezinho.data.api.NotificationApiService
import com.rcdnc.cafezinho.data.repository.NotificationRepositoryImpl
import com.rcdnc.cafezinho.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependências de notificações
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    
    /**
     * Provê NotificationApiService
     */
    @Provides
    @Singleton
    fun provideNotificationApiService(
        apiConfig: ApiConfig,
        authManager: AuthManager
    ): NotificationApiService {
        val authInterceptor = ApiConfig.AuthInterceptor { 
            runCatching { 
                kotlinx.coroutines.runBlocking { authManager.getCurrentToken() }
            }.getOrNull()
        }
        
        val okHttpClient = apiConfig.createOkHttpClient(authInterceptor)
        val retrofit = apiConfig.createRetrofit(okHttpClient)
        
        return retrofit.create(NotificationApiService::class.java)
    }
    
    /**
     * Provê NotificationRepository
     */
    @Provides
    @Singleton
    fun provideNotificationRepository(
        @ApplicationContext context: Context,
        notificationApiService: NotificationApiService,
        authManager: AuthManager
    ): NotificationRepository {
        return NotificationRepositoryImpl(
            context = context,
            notificationApiService = notificationApiService,
            authManager = authManager
        )
    }
    
    /**
     * Provê NotificationRepositoryImpl para FirebaseMessagingService
     */
    @Provides
    @Singleton
    fun provideNotificationRepositoryImpl(
        notificationRepository: NotificationRepository
    ): NotificationRepositoryImpl {
        return notificationRepository as NotificationRepositoryImpl
    }
}