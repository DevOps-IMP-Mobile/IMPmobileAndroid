package com.example.data.mapper

import com.example.domain.model.issue.*
import com.example.network.dto.AwsIssueResponseDto
import com.example.network.dto.AwsIssueCreateRequestDto
//import com.example.network.dto.AwsIssueUpdateRequestDto

/**
 * DTO -> Domain Model 변환
 */
fun AwsIssueResponseDto.toDomain(): AwsIssue {
    return AwsIssue(
        id = this.id,
        title = this.title,
        description = this.description ?: "",
        type = AwsIssueType.fromString(this.type),
        status = AwsIssueStatus.fromString(this.status),
        priority = AwsIssuePriority.fromString(this.priority),
        importance = AwsIssueImportance.fromString(this.importance),
        assigneeId = this.assigneeId,
        assigneeName = this.assigneeName,
        reporterId = this.reporterId,
        reporterName = this.reporterName,
        createdDate = this.createdDate,
        dueDate = this.dueDate,
        updatedDate = this.updateDate,
        repository = this.repository,
        projectNo = this.projectNo
    )
}

/**
 * Domain Model -> Create Request DTO 변환
 */
fun AwsIssueRequest.toCreateRequestDto(): AwsIssueCreateRequestDto {
    return AwsIssueCreateRequestDto(
        title = this.title,
        description = this.description,
        type = this.type.typeName,
        status = this.status.name,
        priority = this.priority.name,
        importance = this.importance.name,
        assigneeId = this.assigneeId,
        assigneeName = this.assigneeName,
        reporterId = this.reporterId,
        reporterName = this.reporterName,
        dueDate = this.dueDate,
        repository = this.repository,
        projectNo = this.projectNo
    )
}

/**
 * Domain Model -> Update Request DTO 변환
 */
//fun AwsIssueRequest.toUpdateRequestDto(): AwsIssueUpdateRequestDto {
//    return AwsIssueUpdateRequestDto(
//        title = this.title,
//        description = this.description,
//        type = this.type.typeName,
//        status = this.status.name,
//        priority = this.priority.name,
//        importance = this.importance.name,
//        assigneeId = this.assigneeId,
//        assigneeName = this.assigneeName,
//        reporterId = this.reporterId,
//        reporterName = this.reporterName,
//        dueDate = this.dueDate,
//        repository = this.repository,
//        projectNo = this.projectNo
//    )
//}