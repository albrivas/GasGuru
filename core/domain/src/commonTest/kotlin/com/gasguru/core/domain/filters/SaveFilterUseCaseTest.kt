package com.gasguru.core.domain.filters

import com.gasguru.core.domain.fakes.FakeFilterRepository
import com.gasguru.core.model.data.FilterType
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveFilterUseCaseTest {

    private lateinit var sut: SaveFilterUseCase
    private lateinit var fakeFilterRepository: FakeFilterRepository

    @BeforeTest
    fun setUp() {
        fakeFilterRepository = FakeFilterRepository()
        sut = SaveFilterUseCase(filterRepository = fakeFilterRepository)
    }

    @Test
    fun savesFilterWithCorrectTypeAndSelection() = runTest {
        val selection = listOf("REPSOL", "CEPSA")

        sut(filterType = FilterType.BRAND, selection = selection)

        assertEquals(1, fakeFilterRepository.updatedFilters.size)
        val savedFilter = fakeFilterRepository.updatedFilters.first()
        assertEquals(FilterType.BRAND, savedFilter.type)
        assertEquals(selection, savedFilter.selection)
    }

    @Test
    fun savingMultipleFiltersTracksAll() = runTest {
        sut(filterType = FilterType.BRAND, selection = listOf("REPSOL"))
        sut(filterType = FilterType.BRAND, selection = listOf("CEPSA"))

        assertEquals(2, fakeFilterRepository.updatedFilters.size)
    }
}
