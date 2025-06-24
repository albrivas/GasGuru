package com.gasguru.core.network.datasource

import com.gasguru.core.testing.CoroutinesTestExtension
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(MockKExtension::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
/**
 * Used "OrderAnnotation" only for testing
 */
class PlacesDataSourceTest {

    private lateinit var sut: PlacesDataSourceImp

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("GIVEN a valid input WHEN getPlaces is called THEN emit a list of places")
    @Order(1)
    fun getPlacesSuccess() = runTest {
        val prediction = mockk<AutocompletePrediction>()
        val expectedList = listOf(prediction)

        val response = mockk<FindAutocompletePredictionsResponse> {
            every { autocompletePredictions } returns expectedList
        }

        val completedTask = Tasks.forResult(response)

        val placesClient = mockk<PlacesClient> {
            every { findAutocompletePredictions(any()) } returns completedTask
        }

        sut = PlacesDataSourceImp(placesClient)

        val result = sut.getPlaces("Talavera", "ES").first()
        Assertions.assertEquals(expectedList, result)

    }

    @Test
    @DisplayName("GIVEN a failings PlaceClient WHEN getPlaces is called THEN emit an empty list")
    @Order(2)
    fun getPlacesError() = runTest {
        val exception = ApiException(Status.RESULT_INTERNAL_ERROR)
        val failedTask = Tasks.forException<FindAutocompletePredictionsResponse>(exception)

        val placesClient = mockk<PlacesClient> {
            every { findAutocompletePredictions(any()) } returns failedTask
        }

        sut = PlacesDataSourceImp(placesClient)

        val result = sut.getPlaces("Talavera", "ES").first()

        Assertions.assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName("GIVEN a place id WHEN getLocationPlace THEN emit a location")
    @Order(3)
    fun getLocationPlaceSuccess() = runTest {
        val mockLocation = LatLng(40.4168, -3.7038)

        val mockPlace = mockk<com.google.android.libraries.places.api.model.Place> {
            every { location } returns mockLocation
        }

        val response = mockk<FetchPlaceResponse>(relaxed = true) {
            every { place } returns mockPlace
        }

        val completedTask = Tasks.forResult(response)

        val placesClient = mockk<PlacesClient> {
            every { fetchPlace(any()) } returns completedTask
        }

        val expectedLatLng = LatLng(mockLocation.latitude, mockLocation.longitude)
        sut = PlacesDataSourceImp(placesClient)

        val result = sut.getLocationPlace("12312ln").first()
        Assertions.assertEquals(expectedLatLng, result)
    }

    @Test
    @DisplayName("GIVEN a failing PlaceClient WHEN getLocationPlace THEN emit null")
    @Order(4)
    fun getLocationPlaceError() = runTest {
        val exception = ApiException(Status.RESULT_INTERNAL_ERROR)

        val completedTask = Tasks.forException<FetchPlaceResponse>(exception)

        val placesClient = mockk<PlacesClient> {
            every { fetchPlace(any()) } returns completedTask
        }

        sut = PlacesDataSourceImp(placesClient)

        val result = sut.getLocationPlace("12312ln").first()
        Assertions.assertTrue(result == null)
    }
}