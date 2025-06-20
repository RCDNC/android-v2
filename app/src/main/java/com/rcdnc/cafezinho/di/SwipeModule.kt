package com.rcdnc.cafezinho.di

import com.rcdnc.cafezinho.features.swipe.data.repository.UserRepositoryImpl
import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SwipeModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}