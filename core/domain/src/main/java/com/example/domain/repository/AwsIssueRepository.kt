package com.example.domain.repository

import com.example.domain.model.issue.AwsIssue
import com.example.domain.model.issue.AwsIssueRequest

interface AwsIssueRepository {

    /**
     * 이슈 조회
     */
    suspend fun getIssue(id: Long): Result<AwsIssue>

    /**
     * 이슈 생성
     */
    suspend fun createIssue(request: AwsIssueRequest): Result<AwsIssue>

    /**
     * 이슈 수정
     */
    suspend fun updateIssue(id: Long, request: AwsIssueRequest): Result<AwsIssue>

    /**
     * 이슈 삭제
     */
    suspend fun deleteIssue(id: Long): Result<Unit>
}