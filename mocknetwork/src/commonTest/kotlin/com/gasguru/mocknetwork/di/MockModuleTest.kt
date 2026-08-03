package com.gasguru.mocknetwork.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.model.data.AppEnvironment
import com.gasguru.mocknetwork.MockRemoteDataSource
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

class MockModuleTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun buildKoin() = koinApplication {
        modules(
            // Bind as CoroutineDispatcher (not the inferred TestDispatcher), matching the
            // supertype MockModule.kt resolves via get<CoroutineDispatcher>(named(...)).
            module { single<CoroutineDispatcher>(named(KoinQualifiers.IO_DISPATCHER)) { UnconfinedTestDispatcher() } },
            mockModule(),
        )
    }.koin

    @Test
    fun `GIVEN the mock module WHEN resolving AppEnvironment THEN it provides AppEnvironment Mock`() {
        buildKoin().get<AppEnvironment>() shouldBe AppEnvironment.Mock
    }

    @Test
    fun `GIVEN the mock module WHEN resolving MockRemoteDataSource THEN it resolves an instance`() {
        assertNotNull(buildKoin().get<MockRemoteDataSource>())
    }
}
