package com.gasguru.core.data.repository.filter

import com.gasguru.core.database.dao.FilterDao
import com.gasguru.core.database.model.FilterEntity
import com.gasguru.core.database.model.asExternalModel
import com.gasguru.core.model.data.Filter
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor(
    private val dao: FilterDao,
) : FilterRepository {
    override val getFilters: Flow<List<Filter>>
        get() = dao.getFilters().map { it.asExternalModel() }
            .catch {
                listOf(
                    Filter(FilterType.NEARBY, listOf("10")),
                    Filter(FilterType.NEARBY, emptyList()),
                    Filter(FilterType.SCHEDULE, emptyList())
                )
            }

    override suspend fun insertOrUpdateFilter(filterType: FilterType, selection: List<String>) {
        if (dao.isFilterExist(filterType = filterType) == 1) {
            dao.updateFilterByType(filterType, selection)
        } else {
            dao.insertFilter(FilterEntity(type = filterType, selection = selection))
        }
    }
}
