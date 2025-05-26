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
import javax.inject.Named
import javax.inject.Singleton

const val CONNECTION_TIMEOUT: Long = 60
const val WRITE_TIMEOUT: Long = 60
const val READ_TIMEOUT: Long = 60
const val BASE_URL: String = "https://sedeaplicaciones.minetur.gob.es/"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        private const val FUEL_API_OK_HTTP_CLIENT = "fuelApiOkHttpClient"
        private const val ROUTE_API_OK_HTTP_CLIENT = "routeApiOkHttpClient"
        private const val FUEL_API_RETROFIT = "fuelApiRetrofit"
        private const val ROUTE_API_RETROFIT = "routeApiRetrofit"
        const val FUEL_API_SERVICE = "fuelApiService"
        const val ROUTE_API_SERVICE = "routeApiService"
    }

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Singleton
    @Provides
    fun providesRoutesInterceptor(@ApplicationContext context: Context): Interceptor =
        RoutesInterceptor(context)

    @Singleton
    @Provides
    @Named(FUEL_API_OK_HTTP_CLIENT)
    fun providesFuelOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    @Named(ROUTE_API_OK_HTTP_CLIENT)
    fun providesRouteOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        routesInterceptor: RoutesInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(routesInterceptor)
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    @Named(FUEL_API_RETROFIT)
    fun provideFuelRetrofit(@Named(FUEL_API_OK_HTTP_CLIENT) okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Singleton
    @Provides
    @Named(ROUTE_API_RETROFIT)
    fun provideRouteRetrofit(@Named(ROUTE_API_OK_HTTP_CLIENT) okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.ROUTE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Singleton
    @Provides
    @Named(FUEL_API_SERVICE)
    fun provideFuelApiService(@Named(FUEL_API_RETROFIT) retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    @Named(ROUTE_API_SERVICE)
    fun provideRouteApiService(@Named(ROUTE_API_RETROFIT) retrofit: Retrofit): RouteApiServices =
        retrofit.create(RouteApiServices::class.java)
}
