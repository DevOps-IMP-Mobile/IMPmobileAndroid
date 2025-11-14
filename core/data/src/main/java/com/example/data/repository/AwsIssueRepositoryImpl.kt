package com.example.data.repository

import com.example.domain.model.issue.AwsIssue
import com.example.domain.model.issue.AwsIssueRequest
import com.example.domain.repository.AwsIssueRepository
import com.example.network.api.AwsIssueApiService
import com.example.data.mapper.toDomain
import com.example.data.mapper.toCreateRequestDto
//import com.example.data.mapper.toUpdateRequestDto
import javax.inject.Inject
import javax.inject.Named

class AwsIssueRepositoryImpl @Inject constructor(
    @Named("aws_issue") private val apiService: AwsIssueApiService
) : AwsIssueRepository {

    override suspend fun getIssue(id: Long): Result<AwsIssue> {
        return try {
            val response = apiService.getIssue(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createIssue(request: AwsIssueRequest): Result<AwsIssue> {
        return try {
            val response = apiService.createIssue(request.toCreateRequestDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateIssue(id: Long, request: AwsIssueRequest): Result<AwsIssue> {
        return try {
            val response = apiService.updateIssue(id, request.toUpdateRequestDto())
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteIssue(id: Long): Result<Unit> {
        return try {
            apiService.deleteIssue(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}