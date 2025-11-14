package com.example.domain.model.user

/**
 * AWS 서버용 사용자 정보
 */
data class AwsUser(
    val id: Long,
    val name: String,
    val userId: String,
    val email: String,
    val interlockEmail: String?
)

/**
 * AWS 사용자 생성 요청
 */
data class AwsUserCreateRequest(
    val name: String,
    val userId: String,
    val email: String,
    val interlockEmail: String?
)

/**
 * AWS 사용자 수정 요청
 */
data class AwsUserUpdateRequest(
    val name: String,
    val email: String,
    val interlockEmail: String?
)