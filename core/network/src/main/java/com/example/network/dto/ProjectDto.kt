package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class ProjectListResponse(
    @SerializedName("list_cnt") val listCnt: String,
    @SerializedName("total_cnt") val totalCnt: String?,
    @SerializedName("list") val list: List<ProjectDto>
)

data class ProjectDto(
    @SerializedName("project_uid") val projectUid: String,
    @SerializedName("project_name") val projectName: String,
    @SerializedName("project_no") val projectNo: String,
    @SerializedName("start_dt") val startDate: String?,
    @SerializedName("end_dt") val endDate: String?,
    @SerializedName("project_manager") val managerId: String?,
    @SerializedName("project_manager_name") val managerName: String?,
    @SerializedName("del_yn") val delYn: String?,
    @SerializedName("standard_yn") val standardYn: String?,
    @SerializedName("project_state_cd") val projectStateCd: String?,
    @SerializedName("sp_uid") val spUid: String?
    // 필요한 필드는 추가로 작성
) 