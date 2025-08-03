package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class ProjectListResponse(
    @SerializedName("list_cnt") val listCnt: String,
    @SerializedName("total_cnt") val totalCnt: String?,
    @SerializedName("list") val list: List<ProjectDto>
)

data class ProjectDto(
    @SerializedName("psc") val psc: String? = null,
    @SerializedName("end_dt") val endDate: String,
    @SerializedName("project_state_cd") val projectStateCd: String? = null,
    @SerializedName("upd_dt") val updDt: String? = null,
    @SerializedName("project_uid") val projectUid: String,
    @SerializedName("standard_yn") val standardYn: String? = null,
    @SerializedName("crtr_dt") val crtrDt: String? = null,
    @SerializedName("project_name") val projectName: String,
    @SerializedName("del_yn") val delYn: String? = null,
    @SerializedName("start_dt") val startDate: String,
    @SerializedName("project_no") val projectNo: String,
    @SerializedName("sp_name") val spName: String? = null,
    @SerializedName("project_manager_name") val managerName: String? = null,
    @SerializedName("sp_uid") val spUid: String? = null,
    @SerializedName("project_manager") val managerId: String? = null,
    @SerializedName("rn") val rn: Int? = null,
    @SerializedName("new_order") val newOrder: String? = null
) 