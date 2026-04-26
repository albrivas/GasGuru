package com.gasguru.core.network.di

import com.gasguru.core.network.BuildConfig
import com.gasguru.core.network.datasource.RoutesDataSource
import com.gasguru.core.network.datasource.RoutesDataSourceImpl
import com.gasguru.core.network.routesPlugin
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val TIMEOUT_MS = 60_000L
private const val ROUTE_BASE_URL = "https://routes.googleapis.com/"

const val ROUTE_HTTP_CLIENT = "routeHttpClient"

val ktorModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single<HttpClient>(named(ROUTE_HTTP_CLIENT)) {
        val context = androidContext()
        HttpClient(OkHttp) {
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MS
                connectTimeoutMillis = TIMEOUT_MS
                socketTimeoutMillis = TIMEOUT_MS
            }
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }
            install(routesPlugin(packageName = context.packageName))
            defaultRequest {
                url(ROUTE_BASE_URL)
            }
        }
    }

    single<RoutesDataSource> {
        RoutesDataSourceImpl(
            httpClient = get<HttpClient>(named(ROUTE_HTTP_CLIENT)),
        )
    }
}
