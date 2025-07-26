package com.example.profile

import com.example.network.dto.MeResponse
import java.text.SimpleDateFormat
import java.util.*

data class UserProfile(
    val userId: String? = null,
    val userName: String? = null,
    val systemRole: String? = null,
    val orgCd: String? = null,
    val telNo: String? = null,
    val email: String? = null,
    val loginUserEmail: String? = null,
    val lastResponseDate: String? = null,
    val groupCode: String? = null,
    val spUid: String? = null,
    val userUid: String? = null,
    val lastProjectNo: String? = null
) {
    companion object {
        fun fromMeResponse(response: MeResponse): UserProfile {
            return UserProfile(
                userId = response.userId,
                userName = response.userName,
                groupCode = response.groupCode,
                spUid = response.spUid,
                userUid = response.userUid,
                lastProjectNo = response.lastProjectNo,
                lastResponseDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            )
        }
    }
}
