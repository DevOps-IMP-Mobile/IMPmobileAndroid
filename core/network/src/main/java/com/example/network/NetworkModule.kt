package com.example.network

import com.example.domain.repository.AuthRepository
import com.example.domain.manager.TokenManager
import com.example.network.api.AuthApiService
import com.example.network.api.AwsIssueApiService
import com.example.network.api.DashboardApiService
import com.example.network.api.ProjectApiService
import com.example.network.api.IssueApiService
import com.example.network.api.MeApiService
import com.example.network.api.CodeApiService
import com.example.network.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://coverdreamit.iptime.org:8401/portal/"
    private const val DASHBOARD_BASE_URL = "http://coverdreamit.iptime.org:8402/"
    private const val CODE_BASE_URL = "http://coverdreamit.iptime.org:8401/portal/"

    private const val UNIFIED_AWS_BASE_URL = "http://spring-env.eba-mdpim7pd.ap-southeast-2.elasticbeanstalk.com/"

    private const val AWS_BASE_URL = "http://spring-env.eba-mdpim7pd.ap-southeast-2.elasticbeanstalk.com/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().also { interceptor ->
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: TokenManager
    ): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 기존 8401/portal/용 Retrofit (Auth, Project, Profile 등)
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProjectApiService(retrofit: Retrofit): ProjectApiService {
        return retrofit.create(ProjectApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMeApiService(retrofit: Retrofit): MeApiService {
        return retrofit.create(MeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCodeApiService(retrofit: Retrofit): CodeApiService {
        return retrofit.create(CodeApiService::class.java)
    }

    // Dashboard 전용 Retrofit (8402 포트)
    @Provides
    @Singleton
    @Named("dashboard")
    fun provideDashboardRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DASHBOARD_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDashboardApiService(
        @Named("dashboard") dashboardRetrofit: Retrofit
    ): DashboardApiService {
        return dashboardRetrofit.create(DashboardApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideIssueApiService(
        @Named("dashboard") dashboardRetrofit: Retrofit
    ): IssueApiService {
        return dashboardRetrofit.create(IssueApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("aws")
    fun provideAwsRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AWS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
//
    // ⭐ AWS Issue API Service
    @Provides
    @Singleton
    @Named("aws_issue")
    fun provideAwsIssueApiService(
        @Named("aws") awsRetrofit: Retrofit
    ): AwsIssueApiService {
        return awsRetrofit.create(AwsIssueApiService::class.java)
    }
//
//    // ⭐ AWS User API Service
//    @Provides
//    @Singleton
//    @Named("aws_user")
//    fun provideAwsUserApiService(
//        @Named("aws") awsRetrofit: Retrofit
//    ): AwsUserApiService {
//        return awsRetrofit.create(AwsUserApiService::class.java)
//    }
//
//    // ⭐ AWS Notification API Service
//    @Provides
//    @Singleton
//    @Named("aws_notification")
//    fun provideAwsNotificationApiService(
//        @Named("aws") awsRetrofit: Retrofit
//    ): AwsNotificationApiService {
//        return awsRetrofit.create(AwsNotificationApiService::class.java)
//    }
}