package com.gasguru.core.domain.maps

import com.gasguru.core.domain.fakes.FakeStaticMapRepository
import com.gasguru.core.model.data.LatLng
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetStaticMapUrlUseCaseTest {

    private lateinit var sut: GetStaticMapUrlUseCase
    private lateinit var fakeStaticMapRepository: FakeStaticMapRepository

    @BeforeTest
    fun setUp() {
        fakeStaticMapRepository = FakeStaticMapRepository()
        sut = GetStaticMapUrlUseCase(staticMapRepository = fakeStaticMapRepository)
    }

    @Test
    fun returnsGeneratedUrl() {
        val location = LatLng(latitude = 40.4, longitude = -3.7)
        fakeStaticMapRepository.urlToReturn = "https://static.map/tile"

        val result = sut(location = location)

        assertEquals("https://static.map/tile", result)
    }

    @Test
    fun delegatesCorrectParamsToRepository() {
        val location = LatLng(latitude = 40.4, longitude = -3.7)

        sut(location = location, zoom = 15, width = 300, height = 200)

        assertEquals(location, fakeStaticMapRepository.lastLocation)
        assertEquals(15, fakeStaticMapRepository.lastZoom)
        assertEquals(300, fakeStaticMapRepository.lastWidth)
        assertEquals(200, fakeStaticMapRepository.lastHeight)
    }

    @Test
    fun usesDefaultParamsWhenNotProvided() {
        val location = LatLng(latitude = 40.4, longitude = -3.7)

        sut(location = location)

        assertEquals(17, fakeStaticMapRepository.lastZoom)
        assertEquals(400, fakeStaticMapRepository.lastWidth)
        assertEquals(240, fakeStaticMapRepository.lastHeight)
    }
}
