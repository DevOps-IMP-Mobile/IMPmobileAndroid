package com.example.network.dto

import com.google.gson.annotations.SerializedName

// 이슈 등록 요청
data class CreateIssueRequest(
    @SerializedName("issue_name")
    val issueName: String,
    @SerializedName("issue_desc")
    val issueDesc: String,
    @SerializedName("issue_type_id")
    val issueTypeId: String,
    @SerializedName("priority_cd")
    val priorityCd: String,
    @SerializedName("importance_cd")
    val importanceCd: String,
    @SerializedName("end_dt")
    val endDt: String,
    @SerializedName("start_dt")
    val startDt: String,
    @SerializedName("issue_dt")
    val issueDt: String,
    @SerializedName("loginId")
    val loginId: String,
    @SerializedName("sp_uid")
    val spUid: String,
    @SerializedName("project_no")
    val projectNo: String,
    // 추가: 담당자 ID/이름
    @SerializedName("charger")
    val charger: String? = null,
    @SerializedName("chargerName")
    val chargerName: String? = null
)

// 이슈 수정 요청
data class UpdateIssueRequest(
    @SerializedName("issue_id")
    val issueId: String,
    @SerializedName("issue_name")
    val issueName: String,
    @SerializedName("issue_type_id")
    val issueTypeId: String,
    @SerializedName("priority_cd")
    val priorityCd: String,
    @SerializedName("importance_cd")
    val importanceCd: String,
    @SerializedName("end_dt")
    val endDt: String,
    @SerializedName("issue_desc")
    val issueDesc: String,
    @SerializedName("start_dt")
    val startDt: String,
    @SerializedName("loginId")
    val loginId: String,
    @SerializedName("sp_uid")
    val spUid: String,
    @SerializedName("project_no")
    val projectNo: String,
    // 추가: 담당자 ID/이름
    @SerializedName("charger")
    val charger: String? = null,
    @SerializedName("chargerName")
    val chargerName: String? = null
)

// 이슈 삭제 요청
data class DeleteIssueRequest(
    @SerializedName("groupCode")
    val groupCode: String,
    @SerializedName("issue_id")
    val issueId: String,
    @SerializedName("loginId")
    val loginId: String,
    @SerializedName("project_no")
    val projectNo: String,
    @SerializedName("sp_uid")
    val spUid: String
)

// CRUD 응답
data class IssueCrudResponse(
    @SerializedName("list_cnt")
    val listCnt: Int,
    @SerializedName("list")
    val list: List<IssueCrudResult>
)

data class IssueCrudResult(
    @SerializedName("cnt")
    val cnt: Int
)

// 이슈 타입 목록 응답
data class IssueTypeListResponse(
    @SerializedName("list_cnt")
    val listCnt: Int,
    @SerializedName("list")
    val list: List<IssueTypeDto>
)

data class IssueTypeDto(
    @SerializedName("issue_type_id")
    val issueTypeId: String,
    @SerializedName("issue_type_name")
    val issueTypeName: String,
    @SerializedName("issue_type_desc")
    val issueTypeDesc: String?,
    @SerializedName("use_yn")
    val useYn: String,
    @SerializedName("sort_order")
    val sortOrder: Int
)

// 코드 목록 응답 (우선순위, 중요도)
data class CodeListResponse(
    @SerializedName("list_cnt")
    val listCnt: Int,
    @SerializedName("list")
    val list: List<CodeDto>
)

data class CodeDto(
    @SerializedName("code_xd")
    val codeId: String,
    @SerializedName("code_name")
    val codeName: String,
    @SerializedName("code_desc")
    val codeDesc: String?,
    @SerializedName("use_yn")
    val useYn: String,
    @SerializedName("sort_order")
    val sortOrder: Int
) 