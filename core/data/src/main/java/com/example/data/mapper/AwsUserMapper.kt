package com.example.data.mapper

import com.example.domain.model.user.*
import com.example.network.dto.AwsUserResponseDto
import com.example.network.dto.AwsUserCreateRequestDto
import com.example.network.dto.AwsUserUpdateRequestDto

fun AwsUserResponseDto.toDomain(): AwsUser {
    return AwsUser(
        id = this.id,
        name = this.name,
        userId = this.userId,
        email = this.email,
        interlockEmail = this.interlockEmail
    )
}

fun AwsUserCreateRequest.toDto(): AwsUserCreateRequestDto {
    return AwsUserCreateRequestDto(
        name = this.name,
        userId = this.userId,
        email = this.email,
        interlockEmail = this.interlockEmail
    )
}

fun AwsUserUpdateRequest.toDto(): AwsUserUpdateRequestDto {
    return AwsUserUpdateRequestDto(
        name = this.name,
        email = this.email,
        interlockEmail = this.interlockEmail
    )
}