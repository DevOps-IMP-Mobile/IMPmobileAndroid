// core/data/src/main/java/com/example/data/di/DataModule.kt
package com.example.data.di

import com.example.data.repository.DashboardRepositoryImpl
import com.example.domain.repository.DashboardRepository
import com.example.data.repository.ProjectRepositoryImpl
import com.example.domain.repository.ProjectRepository
import com.example.data.repository.IssueRepositoryImpl
import com.example.domain.repository.IssueRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        dashboardRepositoryImpl: DashboardRepositoryImpl
    ): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        projectRepositoryImpl: ProjectRepositoryImpl
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindIssueRepository(
        issueRepositoryImpl: IssueRepositoryImpl
    ): IssueRepository
}