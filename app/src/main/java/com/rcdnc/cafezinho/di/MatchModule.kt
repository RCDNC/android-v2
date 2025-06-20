package com.rcdnc.cafezinho.di

import com.rcdnc.cafezinho.features.matches.data.repository.MatchRepositoryImpl
import com.rcdnc.cafezinho.features.matches.domain.repository.MatchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MatchModule {

    @Binds
    @Singleton
    abstract fun bindMatchRepository(
        matchRepositoryImpl: MatchRepositoryImpl
    ): MatchRepository
}