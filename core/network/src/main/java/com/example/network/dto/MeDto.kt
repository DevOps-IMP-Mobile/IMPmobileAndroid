package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class MeResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("groupCode") val groupCode: String?,
    @SerializedName("spUid") val spUid: String?,
    @SerializedName("lastProjectNo") val lastProjectNo: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("userUid") val userUid: String?,
    // 필요한 필드 추가 가능
    @SerializedName("login_type") val loginType: String? = null,
@SerializedName("lastProjectUid") val lastProjectUid: String? = null,
@SerializedName("lastSpUid") val lastSpUid: String? = null,
@SerializedName("lastSpId") val lastSpId: String? = null,
@SerializedName("localeXd") val localeXd: String? = null,
@SerializedName("spId") val spId: String? = null,
@SerializedName("telNo") val telNo: String? = null,
@SerializedName("lastSolution") val lastSolution: String? = null,
@SerializedName("deptKey") val deptKey: String? = null,
@SerializedName("orgCd") val orgCd: String? = null,
@SerializedName("prjUserGroupCd") val prjUserGroupCd: String? = null,
@SerializedName("systemRole") val systemRole: String? = null,
@SerializedName("roleCode") val roleCode: String? = null,
@SerializedName("login_user_email") val loginUserEmail: String? = null,
@SerializedName("email") val email: String? = null,
@SerializedName("lastResponseDate") val lastResponseDate: String? = null
) 