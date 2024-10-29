/*
 * File: RemoteDataSourceTest.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.unitTest
 * Last modified: 12/29/22, 5:32 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.gasguru.core.network.datasource

import com.gasguru.core.network.NetworkModuleTest
import com.gasguru.core.network.stubs.MockApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RemoteDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()

    private val networkModule: NetworkModuleTest = NetworkModuleTest()
    private lateinit var server: MockWebServer
    private lateinit var sut: RemoteDataSourceImp
    private lateinit var mockApi: MockApiResponse

    @Before
    fun setUp() {
        server = networkModule.mockWebServer
        server.start()
        sut = RemoteDataSourceImp(networkModule.apiService)
        mockApi = MockApiResponse()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `get list fuel stations - success`() {
        server.enqueue(mockApi.listFuelStationOK())

        runTest(testDispatcher) {
            val actual = sut.getListFuelStations()

            assert(!actual.isLeft() && actual.isRight())
        }
    }

    @Test
    fun `get list fuel stations - error`() {
        server.enqueue(mockApi.listFuelStationKO())

        runTest(testDispatcher) {
            val actual = sut.getListFuelStations()

            assert(actual.isLeft() && !actual.isRight())
        }
    }
}
