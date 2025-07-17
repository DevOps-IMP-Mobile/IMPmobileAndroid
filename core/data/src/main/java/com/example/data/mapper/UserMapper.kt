package com.example.data.mapper

import com.example.database.entity.UserEntity
import com.example.domain.model.User
import com.example.domain.model.UserRole

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        role = UserRole.valueOf(role),
        profileImageUrl = profileImageUrl,
        isActive = isActive,
        token = token,
        lastLoginAt = lastLoginAt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        role = role.name,
        profileImageUrl = profileImageUrl,
        isActive = isActive,
        token = token,
        lastLoginAt = lastLoginAt,
        createdAt = System.currentTimeMillis()
    )
}
