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
    @SerializedName("issueId") val issueId: String? = null,
    @SerializedName("issueName") val issueName: String,
    @SerializedName("projectNo") val projectNo: String,
    @SerializedName("projectName") val projectName: String? = null,
    @SerializedName("importanceCd") val importanceCd: String? = null,
    @SerializedName("importance") val importance: String,
    @SerializedName("priorityCd") val priorityCd: String? = null,
    @SerializedName("priority") val priority: String,
    @SerializedName("issueStateId") val issueStateId: String? = null,
    @SerializedName("issueStateName") val issueStateName: String,
    @SerializedName("issueStateColor") val issueStateColor: String,
    @SerializedName("issueTypeId") val issueTypeId: String? = null,
    @SerializedName("issueStateProp") val issueStateProp: String? = null,
    @SerializedName("issueTypeName") val issueTypeName: String,
    @SerializedName("startDt") val startDt: String? = null,
    @SerializedName("endDt") val endDt: String,
    @SerializedName("period") val period: String? = null,
    @SerializedName("charger") val charger: String? = null,
    @SerializedName("chargerName") val chargerName: String,
    @SerializedName("crtrDt") val crtrDt: String? = null,
    @SerializedName("crtrId") val crtrId: String? = null,
    @SerializedName("crtrName") val crtrName: String? = null,
    @SerializedName("crtrDtYYYYMMDD") val crtrDtYYYYMMDD: String,
    @SerializedName("updDt") val updDt: String? = null,
    @SerializedName("updId") val updId: String? = null,
    @SerializedName("updName") val updName: String? = null,
    @SerializedName("updDtYYYYMMDD") val updDtYYYYMMDD: String? = null,
    @SerializedName("issueDesc") val issueDesc: String? = null,
    @SerializedName("kanbanId") val kanbanId: String? = null,
    @SerializedName("hashtagList") val hashtagList: String? = null,
    @SerializedName("spUid") val spUid: String? = null,
    @SerializedName("gitSvcId") val gitSvcId: String? = null,
    @SerializedName("repositoryNo") val repositoryNo: String? = null,
    @SerializedName("repositoryName") val repositoryName: String? = null,
    @SerializedName("gitProjectId") val gitProjectId: String? = null,
    @SerializedName("branch") val branch: String? = null,
    @SerializedName("svcIp") val svcIp: String? = null,
    @SerializedName("svcPort") val svcPort: String? = null,
    @SerializedName("svcPath") val svcPath: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("isn") val isn: String,
    @SerializedName("checkDate") val checkDate: String? = null,
    @SerializedName("checkYn") val checkYn: String? = null,
    @SerializedName("progressState") val progressState: String? = null
)