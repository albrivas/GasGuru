package com.gasguru.core.network.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.network.BuildConfig
import com.gasguru.core.network.RoutesInterceptor
import com.gasguru.core.network.retrofit.ApiService
import com.gasguru.core.network.retrofit.RouteApiServices
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECTION_TIMEOUT = 60L
private const val WRITE_TIMEOUT = 60L
private const val READ_TIMEOUT = 60L
private const val BASE_URL = "https://sedeaplicaciones.minetur.gob.es/"
private const val ROUTE_BASE_URL = "https://routes.googleapis.com/"

val networkModule = module {
    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    single<Interceptor> { RoutesInterceptor(context = androidContext()) }

    single<OkHttpClient>(named(KoinQualifiers.FUEL_OK_HTTP)) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    single<OkHttpClient>(named(KoinQualifiers.ROUTE_OK_HTTP)) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<Interceptor>())
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit>(named(KoinQualifiers.FUEL_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(named(KoinQualifiers.FUEL_OK_HTTP)))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    single<Retrofit>(named(KoinQualifiers.ROUTE_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl(ROUTE_BASE_URL)
            .client(get(named(KoinQualifiers.ROUTE_OK_HTTP)))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    single<ApiService> {
        get<Retrofit>(named(KoinQualifiers.FUEL_RETROFIT)).create(ApiService::class.java)
    }

    single<RouteApiServices> {
        get<Retrofit>(named(KoinQualifiers.ROUTE_RETROFIT)).create(RouteApiServices::class.java)
    }
}
