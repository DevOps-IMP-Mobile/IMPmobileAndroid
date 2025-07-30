package com.example.domain.repository

import com.example.domain.model.issue.Issue
import com.example.domain.model.issue.IssueFilter
import com.example.domain.model.issue.IssueSortType
import com.example.domain.model.issue.IssueOptions
import kotlinx.coroutines.flow.Flow

interface IssueRepository {
    suspend fun getIssueList(
        filter: IssueFilter = IssueFilter(),
        sortType: IssueSortType = IssueSortType.PRIORITY
    ): Flow<List<Issue>>
    
    // 이슈 등록
    suspend fun createIssue(
        title: String,
        description: String,
        typeId: String,
        priorityCd: String,
        importanceCd: String,
        startDate: String,
        endDate: String,
        projectNo: String
    ): Result<Boolean>
    
    // 이슈 수정
    suspend fun updateIssue(
        issueId: String,
        title: String,
        description: String,
        typeId: String,
        priorityCd: String,
        importanceCd: String,
        startDate: String,
        endDate: String,
        projectNo: String
    ): Result<Boolean>
    
    // 이슈 삭제
    suspend fun deleteIssue(
        issueId: String,
        projectNo: String
    ): Result<Boolean>
    
    // 이슈 옵션 조회
    suspend fun getIssueOptions(projectNo: String): Result<IssueOptions>
} 