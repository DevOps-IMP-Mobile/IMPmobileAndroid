package com.example.domain.usecase.aws

import com.example.domain.model.issue.AwsIssue
import com.example.domain.model.issue.AwsIssueRequest
import com.example.domain.repository.AwsIssueRepository
import javax.inject.Inject

/**
 * AWS 이슈 조회
 */
class GetAwsIssueUseCase @Inject constructor(
    private val repository: AwsIssueRepository
) {
    suspend operator fun invoke(id: Long): Result<AwsIssue> {
        return repository.getIssue(id)
    }
}

/**
 * AWS 이슈 생성
 */
class CreateAwsIssueUseCase @Inject constructor(
    private val repository: AwsIssueRepository
) {
    suspend operator fun invoke(request: AwsIssueRequest): Result<AwsIssue> {
        return repository.createIssue(request)
    }
}

/**
 * AWS 이슈 수정
 */
class UpdateAwsIssueUseCase @Inject constructor(
    private val repository: AwsIssueRepository
) {
    suspend operator fun invoke(id: Long, request: AwsIssueRequest): Result<AwsIssue> {
        return repository.updateIssue(id, request)
    }
}

/**
 * AWS 이슈 삭제
 */
class DeleteAwsIssueUseCase @Inject constructor(
    private val repository: AwsIssueRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteIssue(id)
    }
}