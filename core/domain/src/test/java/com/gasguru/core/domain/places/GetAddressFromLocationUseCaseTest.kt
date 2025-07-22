package com.gasguru.core.domain.places

import com.gasguru.core.data.repository.geocoder.GeocoderAddress
import com.gasguru.core.testing.CoroutinesTestExtension
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class GetAddressFromLocationUseCaseTest {

    private lateinit var sut: GetAddressFromLocationUseCase
    private lateinit var geocoderAddress: GeocoderAddress

    @BeforeEach
    fun setUp() {
        geocoderAddress = mockk()
        sut = GetAddressFromLocationUseCase(geocoderAddress)
    }

    @Test
    @DisplayName("GIVEN latitude and longitude WHEN call invoke THEN return an address")
    fun getAddressFromLocationSuccess() = runTest {
        val response = "Talavera de la Reina, Spain"

        coEvery {
            geocoderAddress.getAddressFromLocation(30.4, -20.3)
        } returns flowOf(response)

        val result = sut.invoke(latitude = 30.4, longitude = -20.3).first()

        Assertions.assertEquals(response, result)
    }

    @Test
    @DisplayName("GIVEN latitude and longitude WHEN geocoder fails THEN return a null")
    fun getAddressFromLocationError() = runTest {
        val response = null

        coEvery {
            geocoderAddress.getAddressFromLocation(30.4, -20.3)
        } returns flowOf(response)

        val result = sut.invoke(latitude = 30.4, longitude = -20.3).first()

        Assertions.assertEquals(response, result)
    }
}
