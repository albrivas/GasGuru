package com.albrivas.fuelpump.data

import app.cash.turbine.test
import arrow.core.right
import com.albrivas.fuelpump.core.data.mapper.asEntity
import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import com.albrivas.fuelpump.core.network.datasource.RemoteDataSource
import com.albrivas.fuelpump.core.network.model.NetworkFuelStation
import com.albrivas.fuelpump.core.network.model.NetworkPriceFuelStation
import com.albrivas.fuelpump.core.testing.CoroutinesTestRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

/**
 * Unit tests for [OfflineFuelStationRepository].
 */

//TODO: Modify all test because something is not correct here
@ExperimentalCoroutinesApi
class OfflineFuelStationRepositoryTest {

    private val dao = mockk<FuelStationDao>()
    private val dataSource = mockk<RemoteDataSource>()

    private lateinit var repository: OfflineFuelStationRepository

    @get: Rule
    val dispatcherRule = CoroutinesTestRule()


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { dao.getFuelStations() } answers { flowList.map { it.map { it.asEntity() } } }
        //coEvery { dao.getFuelStations() } answers { flowOf(listOf()) }
        repository = OfflineFuelStationRepository(dao, dataSource)
    }

    @Test
    fun `get fuel station list OK`() = runTest {
        coEvery { dataSource.getListFuelStations() } answers { networkFuelStation.right() }
        coEvery { dao.insertFuelStation(any()) } answers { networkFuelStation.listPriceFuelStation }

        repository.addAllStations()

        val result = networkFuelStation.listPriceFuelStation.map { it.asEntity() }
        repository.listFuelStation.test {
            assertEquals(awaitItem(), result.map { it.locality })
            awaitComplete()
        }
    }


    private val listFuelStation = listOf(
        NetworkPriceFuelStation(
            "",
            "",
            "",
            "",
            "",
            "",
            "1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        ),
        NetworkPriceFuelStation(
            "",
            "",
            "",
            "",
            "",
            "",
            "2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
    )

    private val flowList = flowOf(listFuelStation)
    private val networkFuelStation = NetworkFuelStation(Date().toString(), listFuelStation)
}
