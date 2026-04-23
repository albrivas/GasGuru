package com.gasguru.core.data.repository.filter

import com.gasguru.core.model.data.Filter
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.flow.Flow

interface FilterRepository {
    val getFilters: Flow<List<Filter>>
    suspend fun insertOrUpdateFilter(filterType: FilterType, selection: List<String>)
}
