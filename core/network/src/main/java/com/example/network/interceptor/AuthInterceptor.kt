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
        
        // URL 로그 출력
        Log.d("AuthInterceptor", "=== API 호출 URL ===")
        Log.d("AuthInterceptor", "URL: ${originalRequest.url}")
        Log.d("AuthInterceptor", "Method: ${originalRequest.method}")
        Log.d("AuthInterceptor", "Original Headers: ${originalRequest.headers}")
        
        val token = runBlocking { tokenManager.getToken() }
        val newRequest = if (!token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "토큰 추가: ${token.take(10)}...")
            val requestWithHeaders = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")  // Content-Type 헤더 추가
                .build()
            Log.d("AuthInterceptor", "Final Headers: ${requestWithHeaders.headers}")
            requestWithHeaders
        } else {
            Log.w("AuthInterceptor", "토큰이 없음 - 인증 헤더 추가하지 않음")
            val requestWithHeaders = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")  // Content-Type 헤더 추가
                .build()
            Log.d("AuthInterceptor", "Final Headers: ${requestWithHeaders.headers}")
            requestWithHeaders
        }
        return chain.proceed(newRequest)
    }
}