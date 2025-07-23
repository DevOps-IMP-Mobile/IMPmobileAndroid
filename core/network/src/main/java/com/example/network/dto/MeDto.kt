package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class MeResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("groupCode") val groupCode: String?,
    @SerializedName("spUid") val spUid: String?,
    @SerializedName("lastProjectNo") val lastProjectNo: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("userUid") val userUid: String?
    // 필요한 필드 추가 가능
) 