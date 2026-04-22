package com.gasguru.core.domain.places

import com.gasguru.core.domain.fakes.FakeGeocoderAddress
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAddressFromLocationUseCaseTest {

    private lateinit var sut: GetAddressFromLocationUseCase
    private lateinit var fakeGeocoderAddress: FakeGeocoderAddress

    @BeforeTest
    fun setUp() {
        fakeGeocoderAddress = FakeGeocoderAddress()
        sut = GetAddressFromLocationUseCase(geocoderAddress = fakeGeocoderAddress)
    }

    @Test
    fun returnsAddressWhenGeocoderSucceeds() = runTest {
        fakeGeocoderAddress.address = "Talavera de la Reina, Spain"

        val result = sut.invoke(latitude = 30.4, longitude = -20.3).first()

        assertEquals("Talavera de la Reina, Spain", result)
    }

    @Test
    fun returnsNullWhenGeocoderReturnsNull() = runTest {
        fakeGeocoderAddress.address = null

        val result = sut.invoke(latitude = 30.4, longitude = -20.3).first()

        assertEquals(null, result)
    }
}
