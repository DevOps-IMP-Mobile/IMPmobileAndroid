package com.example.domain.repository

import com.example.domain.model.project.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    suspend fun getProjectList(): Flow<List<Project>>
} 