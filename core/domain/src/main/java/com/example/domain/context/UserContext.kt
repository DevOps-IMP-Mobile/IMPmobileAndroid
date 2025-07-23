package com.example.domain.context

data class UserContext(
    var userId: String? = null,
    var groupCode: String? = null,
    var spUid: String? = null,
    var lastProjectNo: String? = null,
    var userName: String? = null,
    var userUid: String? = null
) {
    companion object {
        val instance: UserContext by lazy { UserContext() }
    }
} 