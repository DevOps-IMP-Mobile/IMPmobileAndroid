// core/network/src/main/java/com/example/network/dto/DashboardDto.kt
package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("list_cnt") val listCnt: String,
    @SerializedName("total_cnt") val totalCnt: String?,
    @SerializedName("list") val list: List<IssueApiDto>
)

data class IssueApiDto(
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
)