package com.gasguru.core.domain.filters

import com.gasguru.core.data.repository.filter.FilterRepository
import com.gasguru.core.model.data.FilterType

class SaveFilterUseCase(
    private val filterRepository: FilterRepository,
) {
    suspend operator fun invoke(filterType: FilterType, selection: List<String>): Unit =
        filterRepository.insertOrUpdateFilter(filterType = filterType, selection = selection)
}
