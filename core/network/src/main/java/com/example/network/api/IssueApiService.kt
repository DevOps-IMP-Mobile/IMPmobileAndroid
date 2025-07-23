package com.example.network.api

import com.example.network.dto.IssueListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IssueApiService {
    @GET("its/devops/issuemgr/getMyIssueList")
    suspend fun getMyIssueList(
        @Query("project_no") projectNo: String? = null,
        @Query("sp_uid") spUid: String? = null,
        @Query("loginId") loginId: String? = null,
        @Query("groupCode") groupCode: String? = null,
        @Query("srch_crtr_dt") srchCrtrDt: String? = null,
        @Query("srch_delay") srchDelay: String? = null,
        @Query("srch_not_end") srchNotEnd: String? = null
    ): IssueListResponse
} 