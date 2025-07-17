package com.example.domain.model

data class User(
    val id: String,
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.DEVELOPER,
    val profileImageUrl: String? = null,
    val isActive: Boolean = true,
    val token: String? = null,
    val lastLoginAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    ADMIN, DEVELOPER, TESTER, MANAGER
}