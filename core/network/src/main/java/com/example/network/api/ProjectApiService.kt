package com.example.network.api

import com.example.network.dto.ProjectListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProjectApiService {
    @GET("pt/projects")
    suspend fun getProjectList(
        @Query("gc_chk") gcChk: String? = null,
        @Query("id_chk") idChk: String? = null,
        @Query("user_id") userId: String? = null,
        @Query("del_yn") delYn: String? = null,
        @Query("sp_uid") spUid: String? = null,
        @Query("search_type") searchType: String? = null,
        @Query("search_type_state") searchTypeState: String? = null,
        @Query("search_value") searchValue: String? = null
    ): ProjectListResponse
} 