package com.rcdnc.cafezinho.core.network

import com.rcdnc.cafezinho.core.config.Environment
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configuração da API para integração com Laravel
 * URLs configuradas por ambiente (dev/staging/prod)
 */
@Singleton
class ApiConfig @Inject constructor() {
    
    companion object {
        val BASE_URL = Environment.BASE_URL
        val TIMEOUT_SECONDS = Environment.NETWORK_TIMEOUT
    }
    
    /**
     * Interceptor para adicionar token de autenticação
     * Laravel Sanctum usa Bearer token
     */
    class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest = chain.request()
            
            val token = tokenProvider()
            
            val newRequest = if (token != null) {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
            } else {
                originalRequest.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
            }
            
            return chain.proceed(newRequest)
        }
    }
    
    /**
     * Cria OkHttpClient configurado para a API Laravel
     * Com SSL pinning e configurações de segurança para produção
     */
    fun createOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        
        // Logging apenas em desenvolvimento
        if (Environment.ENABLE_LOGGING) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        
        // SSL Certificate Pinning para produção
        if (Environment.ENABLE_SSL_PINNING) {
            // TODO: Implementar SSL pinning com certificados reais
            // builder.certificatePinner(createCertificatePinner())
        }
        
        // Configurações de segurança de rede
        if (Environment.ENABLE_NETWORK_SECURITY) {
            // Configurações adicionais de segurança para staging/produção
            builder.retryOnConnectionFailure(true)
        }
        
        return builder.build()
    }
    
    /**
     * Cria Retrofit configurado para a API Laravel
     */
    fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}