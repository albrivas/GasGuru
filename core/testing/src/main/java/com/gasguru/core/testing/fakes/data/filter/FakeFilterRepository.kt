package com.gasguru.core.testing.fakes.data.filter

import com.gasguru.core.data.repository.filter.FilterRepository
import com.gasguru.core.model.data.Filter
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeFilterRepository(
    initialFilters: List<Filter> = emptyList(),
) : FilterRepository {

    private val filtersFlow = MutableStateFlow(initialFilters)

    val updatedFilters = mutableListOf<Filter>()

    override val getFilters: Flow<List<Filter>> = filtersFlow

    override suspend fun insertOrUpdateFilter(filterType: FilterType, selection: List<String>) {
        val filter = Filter(type = filterType, selection = selection)
        updatedFilters.add(filter)
        filtersFlow.update { filters ->
            filters.filterNot { it.type == filterType } + filter
        }
    }

    fun setFilters(filters: List<Filter>) {
        filtersFlow.value = filters
    }
}
