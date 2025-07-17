package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val email: String = "",
    val role: String = "DEVELOPER",
    val profileImageUrl: String? = null,
    val isActive: Boolean = true,
    val token: String? = null,
    val lastLoginAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)