package com.example.domain.usecase.issue

import com.example.domain.repository.IssueRepository
import javax.inject.Inject

class CreateIssueUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        typeId: String,
        priorityCd: String,
        importanceCd: String,
        startDate: String,
        endDate: String,
        projectNo: String
    ) = issueRepository.createIssue(
        title = title,
        description = description,
        typeId = typeId,
        priorityCd = priorityCd,
        importanceCd = importanceCd,
        startDate = startDate,
        endDate = endDate,
        projectNo = projectNo
    )
}

class UpdateIssueUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(
        issueId: String,
        title: String,
        description: String,
        typeId: String,
        priorityCd: String,
        importanceCd: String,
        startDate: String,
        endDate: String,
        projectNo: String,
        statusCd: String? = null  // ✅ 추가 (optional)
    ): Result<Boolean> {
        return issueRepository.updateIssue(
            issueId = issueId,
            title = title,
            description = description,
            typeId = typeId,
            priorityCd = priorityCd,
            importanceCd = importanceCd,
            startDate = startDate,
            endDate = endDate,
            projectNo = projectNo,
            statusCd = statusCd  // ✅ 전달
        )
    }
}
class DeleteIssueUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(
        issueId: String,
        projectNo: String
    ) = issueRepository.deleteIssue(
        issueId = issueId,
        projectNo = projectNo
    )
}

class GetIssueOptionsUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(projectNo: String) = issueRepository.getIssueOptions(projectNo)
} 