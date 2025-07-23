// core/network/src/main/java/com/example/network/api/DashboardApiService.kt
package com.example.network.api

import com.example.network.dto.DashboardResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApiService {
    @GET("its/devops/issuemgr/getMyIssueList")
    suspend fun getMyIssueList(
        @Query("project_no") projectNo: String = "",
        @Query("sp_uid") spUid: String = "",
        @Query("loginId") loginId: String = "",
        @Query("groupCode") groupCode: String = "",
        @Query("srch_crtr_dt") srchCrtrDt: String = "",
        @Query("srch_delay") srchDelay: String = "",
        @Query("srch_not_end") srchNotEnd: String = ""
    ): DashboardResponse
}