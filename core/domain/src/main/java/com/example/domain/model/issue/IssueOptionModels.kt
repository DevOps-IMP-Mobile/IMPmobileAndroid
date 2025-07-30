package com.example.domain.model.issue

data class IssueTypeOption(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val sortOrder: Int
)

data class PriorityOption(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val sortOrder: Int
)

data class ImportanceOption(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val sortOrder: Int
)

data class IssueOptions(
    val issueTypes: List<IssueTypeOption>,
    val priorities: List<PriorityOption>,
    val importances: List<ImportanceOption>
) 