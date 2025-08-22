package com.gasguru.core.network.di

import android.content.Context
import com.gasguru.core.network.BuildConfig
import com.gasguru.core.network.RoutesInterceptor
import com.gasguru.core.network.retrofit.ApiService
import com.gasguru.core.network.retrofit.RouteApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val CONNECTION_TIMEOUT = 60L
private const val WRITE_TIMEOUT = 60L
private const val READ_TIMEOUT = 60L
private const val BASE_URL = "https://sedeaplicaciones.minetur.gob.es/"
private const val ROUTE_BASE_URL = "https://routes.googleapis.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideRoutesInterceptor(@ApplicationContext context: Context): Interceptor =
        RoutesInterceptor(context)

    @Provides
    @Singleton
    @FuelApi
    fun provideFuelOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @RouteApi
    fun provideRouteOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        routesInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(routesInterceptor)
        .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @FuelApi
    fun provideFuelRetrofit(@FuelApi okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @RouteApi
    fun provideRouteRetrofit(@RouteApi okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(ROUTE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @FuelApi
    fun provideFuelApiService(@FuelApi retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    @RouteApi
    fun provideRouteApiService(@RouteApi retrofit: Retrofit): RouteApiServices =
        retrofit.create(RouteApiServices::class.java)
}
