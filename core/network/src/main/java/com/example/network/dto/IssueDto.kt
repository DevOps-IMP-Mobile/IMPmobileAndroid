package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class IssueListResponse(
    @SerializedName("list_cnt") val listCnt: String,
    @SerializedName("total_cnt") val totalCnt: String?,
    @SerializedName("list") val list: List<IssueDto>
)

data class IssueDto(
    @SerializedName("issueUid") val issueUid: String,
    @SerializedName("issueName") val issueName: String,
    @SerializedName("projectNo") val projectNo: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("issueStateName") val issueStateName: String,
    @SerializedName("issueStateColor") val issueStateColor: String,
    @SerializedName("issueTypeName") val issueTypeName: String,
    @SerializedName("chargerName") val chargerName: String,
    @SerializedName("crtrDtYYYYMMDD") val crtrDtYYYYMMDD: String,
    @SerializedName("endDt") val endDt: String,
    @SerializedName("isn") val isn: String
    // 필요한 필드 추가 가능
) 