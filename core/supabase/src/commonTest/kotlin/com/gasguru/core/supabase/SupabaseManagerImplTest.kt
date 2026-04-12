package com.gasguru.core.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SupabaseManagerImplTest {

    private lateinit var mockEngine: MockEngine

    @AfterTest
    fun tearDown() {
        mockEngine.close()
    }

    @Test
    fun addPriceAlertSucceeds() = runTest {
        mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel("[]"),
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val sut = buildSut()

        sut.addPriceAlert(
            stationId = 1,
            onesignalPlayerId = "player-id",
            fuelType = "gasoil_a",
            lastNotifiedPrice = 1.399,
        )
        // No exception → test passes
    }

    @Test
    fun removePriceAlertSucceeds() = runTest {
        mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel("[]"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val sut = buildSut()

        sut.removePriceAlert(stationId = 1)
        // No exception → test passes
    }

    @Test
    fun addPriceAlertThrowsOnServerError() = runTest {
        mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel("Internal Server Error"),
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
            )
        }
        val sut = buildSut()

        assertFailsWith<Exception> {
            sut.addPriceAlert(
                stationId = 1,
                onesignalPlayerId = "player-id",
                fuelType = "gasoil_a",
                lastNotifiedPrice = 1.399,
            )
        }
    }

    private fun buildSut(): SupabaseManagerImpl = SupabaseManagerImpl(
        supabaseClient = createSupabaseClient(
            supabaseUrl = "https://fake.supabase.co",
            supabaseKey = "fake-key",
        ) {
            install(Postgrest)
            httpEngine = mockEngine
        },
    )
}
