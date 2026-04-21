package com.gasguru.data.fakes

import com.gasguru.core.database.dao.FilterDao
import com.gasguru.core.database.model.FilterEntity
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeFilterDao(
    initialFilters: List<FilterEntity> = emptyList(),
) : FilterDao {

    private val filtersFlow = MutableStateFlow(initialFilters)

    override fun getFilters(): Flow<List<FilterEntity>> = filtersFlow

    override suspend fun insertFilter(filter: FilterEntity) {
        filtersFlow.update { list ->
            list.filterNot { it.type == filter.type } + filter
        }
    }

    override suspend fun updateFilterByType(filterType: FilterType, newSelection: List<String>) {
        filtersFlow.update { list ->
            list.map { if (it.type == filterType) it.copy(selection = newSelection) else it }
        }
    }

    override suspend fun isFilterExist(filterType: FilterType): Int =
        filtersFlow.value.count { it.type == filterType }
}
