package com.example.network.api

import com.example.network.dto.IssueListResponse
import com.example.network.dto.CreateIssueRequest
import com.example.network.dto.UpdateIssueRequest
import com.example.network.dto.DeleteIssueRequest
import com.example.network.dto.IssueCrudResponse
import com.example.network.dto.IssueTypeListResponse
import retrofit2.http.*

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
    
    @GET("its/devops/issuemgr/getIssueList")
    suspend fun getIssueList(
        @Query("srch_issue_name") srchIssueName: String? = null,
        @Query("srch_crtr_dt") srchCrtrDt: String? = null,
        @Query("srch_not_end") srchNotEnd: String? = null,
        @Query("groupCode") groupCode: String? = null,
        @Query("loginId") loginId: String? = null,
        @Query("project_no") projectNo: String? = null,
        @Query("sp_uid") spUid: String? = null
    ): IssueListResponse
    
    // 이슈 등록
    @POST("its/devops/issuemgr/createIssue")
    suspend fun createIssue(
        @Query("issue_name") issueName: String,
        @Query("issue_desc") issueDesc: String,
        @Query("issue_type_id") issueTypeId: String,
        @Query("priority_cd") priorityCd: String,
        @Query("importance_cd") importanceCd: String,
        @Query("end_dt") endDt: String,
        @Query("start_dt") startDt: String,
        @Query("issue_dt") issueDt: String,
        @Query("loginId") loginId: String,
        @Query("sp_uid") spUid: String,
        @Query("project_no") projectNo: String,
        @Query("charger") charger: String? = null,
        @Query("chargerName") chargerName: String? = null
    ): IssueCrudResponse
    
    // 이슈 수정
    @POST("its/devops/issuemgr/updateIssue")
    suspend fun updateIssue(
        @Query("issue_id") issueId: String,
        @Query("issue_name") issueName: String,
        @Query("issue_type_id") issueTypeId: String,
        @Query("priority_cd") priorityCd: String,
        @Query("importance_cd") importanceCd: String,
        @Query("end_dt") endDt: String,
        @Query("issue_desc") issueDesc: String,
        @Query("start_dt") startDt: String,
        @Query("loginId") loginId: String,
        @Query("sp_uid") spUid: String,
        @Query("project_no") projectNo: String,
        @Query("charger") charger: String? = null,
        @Query("chargerName") chargerName: String? = null,
        @Query("issue_state_id") issueStateId: String? = null
    ): IssueCrudResponse
    
    // 이슈 삭제
    @DELETE("its/devops/issuemgr/deleteIssue")
    suspend fun deleteIssue(
        @Query("groupCode") groupCode: String,
        @Query("issue_id") issueId: String,
        @Query("loginId") loginId: String,
        @Query("project_no") projectNo: String,
        @Query("sp_uid") spUid: String
    ): IssueCrudResponse
    
    // 이슈 타입 조회
    @GET("its/pj/issuetypemgr/getIssueTypeList")
    suspend fun getIssueTypeList(
        @Query("sp_uid") spUid: String,
        @Query("project_no") projectNo: String,
        @Query("loginId") loginId: String,
        @Query("id_chk") idChk: String
    ): IssueTypeListResponse
} 