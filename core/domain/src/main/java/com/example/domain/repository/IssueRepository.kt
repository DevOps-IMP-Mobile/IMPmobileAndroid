package com.example.domain.repository

import com.example.domain.model.issue.Issue
import com.example.domain.model.issue.IssueFilter
import com.example.domain.model.issue.IssueSortType
import kotlinx.coroutines.flow.Flow

interface IssueRepository {
    suspend fun getIssueList(
        filter: IssueFilter = IssueFilter(),
        sortType: IssueSortType = IssueSortType.PRIORITY
    ): Flow<List<Issue>>
} 