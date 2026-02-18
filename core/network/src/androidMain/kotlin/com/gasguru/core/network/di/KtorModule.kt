package com.gasguru.core.network.di

import android.content.Context
import com.gasguru.core.network.BuildConfig
import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.datasource.RemoteDataSourceImp
import com.gasguru.core.network.datasource.RoutesDataSource
import com.gasguru.core.network.datasource.RoutesDataSourceImpl
import com.gasguru.core.network.routesPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

private const val TIMEOUT_MS = 60_000L
private const val BASE_URL = "https://sedeaplicaciones.minetur.gob.es/"
private const val ROUTE_BASE_URL = "https://routes.googleapis.com/"

@Module
@InstallIn(SingletonComponent::class)
object KtorModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    @FuelApi
    fun provideFuelHttpClient(json: Json): HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MS
            connectTimeoutMillis = TIMEOUT_MS
            socketTimeoutMillis = TIMEOUT_MS
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
        }
        defaultRequest {
            url(BASE_URL)
        }
    }

    @Provides
    @Singleton
    @RouteApi
    fun provideRouteHttpClient(
        json: Json,
        @ApplicationContext context: Context,
    ): HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MS
            connectTimeoutMillis = TIMEOUT_MS
            socketTimeoutMillis = TIMEOUT_MS
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
        }
        install(routesPlugin(packageName = context.packageName, isDebug = BuildConfig.DEBUG))
        defaultRequest {
            url(ROUTE_BASE_URL)
        }
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(@FuelApi httpClient: HttpClient): RemoteDataSource =
        RemoteDataSourceImp(httpClient = httpClient)

    @Provides
    @Singleton
    fun provideRoutesDataSource(@RouteApi httpClient: HttpClient): RoutesDataSource =
        RoutesDataSourceImpl(httpClient = httpClient)
}