package com.example.data.di

import com.example.data.repository.AuthRepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.data.manager.TokenManagerImpl
import com.example.data.repository.MockUnifiedRepositoryImpl
import com.example.domain.manager.TokenManager
import com.example.domain.repository.UnifiedRepository
import com.example.network.api.MeApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenManager(
        tokenManagerImpl: TokenManagerImpl
    ): TokenManager

}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvidesModule {

    @Provides
    @Singleton
    fun provideUnifiedRepository(): UnifiedRepository {
        return MockUnifiedRepositoryImpl()
    }
}