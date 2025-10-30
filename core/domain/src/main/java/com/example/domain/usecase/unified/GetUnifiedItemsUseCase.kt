package com.example.domain.usecase.unified

import com.example.domain.model.unified.UnifiedItem
import com.example.domain.model.unified.ItemSource
import com.example.domain.repository.UnifiedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnifiedItemsUseCase @Inject constructor(
    private val unifiedRepository: UnifiedRepository
) {
    suspend operator fun invoke(
        startDate: String,
        endDate: String,
        sources: List<ItemSource> = ItemSource.values().toList()
    ): Flow<List<UnifiedItem>> {
        return unifiedRepository.getAllItems(startDate, endDate, sources)
    }
}