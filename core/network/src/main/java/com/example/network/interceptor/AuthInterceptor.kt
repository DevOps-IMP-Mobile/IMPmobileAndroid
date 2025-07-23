package com.example.network.interceptor

import com.example.domain.manager.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import android.util.Log

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { tokenManager.getToken() }
        val newRequest = if (!token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "토큰 추가: ${token.take(10)}...")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.w("AuthInterceptor", "토큰이 없음 - 인증 헤더 추가하지 않음")
            originalRequest
        }
        return chain.proceed(newRequest)
    }
}