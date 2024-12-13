package com.gasguru.core.domain

import com.gasguru.core.data.repository.FilterRepository
import com.gasguru.core.model.data.FilterType
import javax.inject.Inject

class SaveFilterUseCase @Inject constructor(
    private val filterRepository: FilterRepository,
) {
    suspend operator fun invoke(filterType: FilterType, selection: List<String>): Unit =
        filterRepository.insertOrUpdateFilter(filterType = filterType, selection = selection)
}
