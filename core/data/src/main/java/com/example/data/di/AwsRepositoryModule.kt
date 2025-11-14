package com.example.data.di

import com.example.data.repository.AwsIssueRepositoryImpl
import com.example.data.repository.AwsUserRepositoryImpl
import com.example.data.repository.AwsNotificationRepositoryImpl
import com.example.domain.repository.AwsIssueRepository
import com.example.domain.repository.AwsUserRepository
import com.example.domain.repository.AwsNotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AwsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAwsIssueRepository(
        impl: AwsIssueRepositoryImpl
    ): AwsIssueRepository

    @Binds
    @Singleton
    abstract fun bindAwsUserRepository(
        impl: AwsUserRepositoryImpl
    ): AwsUserRepository

    @Binds
    @Singleton
    abstract fun bindAwsNotificationRepository(
        impl: AwsNotificationRepositoryImpl
    ): AwsNotificationRepository
}