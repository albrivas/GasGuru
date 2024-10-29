package com.gasguru.data

import com.gasguru.core.data.repository.OfflineFuelStationRepository
import com.gasguru.core.testing.CoroutinesTestRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

/**
 * Unit tests for [OfflineFuelStationRepository].
 */

@ExperimentalCoroutinesApi
class OfflineFuelStationRepositoryTest {

    @get: Rule
    val dispatcherRule = CoroutinesTestRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }
}
