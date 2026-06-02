package com.gasguru.core.data.repository.geocoder

import android.content.Context
import android.location.Address
import android.location.Geocoder
import app.cash.turbine.test
import com.gasguru.core.testing.CoroutinesTestExtension
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@DisplayName("GeocoderAddressImpl")
class GeocoderAddressImplTest {

    private lateinit var sut: GeocoderAddressImpl

    @BeforeEach
    fun setUp() {
        mockkConstructor(Geocoder::class)
        sut = GeocoderAddressImpl(
            context = mockk<Context>(),
            ioDispatcher = kotlinx.coroutines.Dispatchers.Unconfined,
        )
    }

    @Test
    @DisplayName(
        """
        GIVEN geocoder returns a valid address
        WHEN getAddressFromLocation is called
        THEN emits the formatted address line
        """
    )
    fun emitsAddressLineWhenGeocoderReturnsResult() = runTest {
        val address = mockk<Address>()
        every { address.getAddressLine(0) } returns "Calle Gran Vía 12, Madrid"
        @Suppress("DEPRECATION")
        every {
            anyConstructed<Geocoder>().getFromLocation(any(), any(), any())
        } returns listOf(address)

        sut.getAddressFromLocation(latitude = 40.416775, longitude = -3.703790).test {
            assertEquals("Calle Gran Vía 12, Madrid", awaitItem())
            awaitComplete()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN geocoder returns empty list
        WHEN getAddressFromLocation is called
        THEN emits null
        """
    )
    fun emitsNullWhenGeocoderReturnsEmptyList() = runTest {
        @Suppress("DEPRECATION")
        every {
            anyConstructed<Geocoder>().getFromLocation(any(), any(), any())
        } returns emptyList()

        sut.getAddressFromLocation(latitude = 40.416775, longitude = -3.703790).test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN geocoder returns null
        WHEN getAddressFromLocation is called
        THEN emits null
        """
    )
    fun emitsNullWhenGeocoderReturnsNull() = runTest {
        @Suppress("DEPRECATION")
        every {
            anyConstructed<Geocoder>().getFromLocation(any(), any(), any())
        } returns null

        sut.getAddressFromLocation(latitude = 40.416775, longitude = -3.703790).test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }
}
