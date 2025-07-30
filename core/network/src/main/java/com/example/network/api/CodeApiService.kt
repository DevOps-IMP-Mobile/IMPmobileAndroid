package com.example.network.api

import com.example.network.dto.CodeListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CodeApiService {
    // 우선순위 조회
    @GET("pt/all/codesubsvcs")
    suspend fun getPriorityList(
        @Query("code_group_id") codeGroupId: String,
        @Query("sp_uid") spUid: String
    ): CodeListResponse
    
    // 중요도 조회
    @GET("pt/all/codesubsvcs")
    suspend fun getImportanceList(
        @Query("code_group_id") codeGroupId: String,
        @Query("sp_uid") spUid: String
    ): CodeListResponse
} 