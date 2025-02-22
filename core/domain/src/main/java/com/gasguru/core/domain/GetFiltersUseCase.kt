package com.gasguru.core.domain

import com.gasguru.core.data.repository.FilterRepository
import com.gasguru.core.model.data.Filter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFiltersUseCase @Inject constructor(
    private val filterRepository: FilterRepository,
) {
    operator fun invoke(): Flow<List<Filter>> = filterRepository.getFilters
}
