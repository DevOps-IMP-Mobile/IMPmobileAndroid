package com.example.domain.repository

import com.example.domain.model.user.AwsUser
import com.example.domain.model.user.AwsUserCreateRequest
import com.example.domain.model.user.AwsUserUpdateRequest

interface AwsUserRepository {

    /**
     * 모든 사용자 조회
     */
    suspend fun getAllUsers(): Result<List<AwsUser>>

    /**
     * 사용자 조회
     */
    suspend fun getUser(id: Long): Result<AwsUser>

    /**
     * 사용자 생성
     */
    suspend fun createUser(request: AwsUserCreateRequest): Result<AwsUser>

    /**
     * 사용자 수정
     */
    suspend fun updateUser(id: Long, request: AwsUserUpdateRequest): Result<AwsUser>

    /**
     * 사용자 삭제
     */
    suspend fun deleteUser(id: Long): Result<Unit>
}