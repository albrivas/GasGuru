package com.gasguru.di

import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.mocknetwork.MockRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MockDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(
        mockRemoteDataSource: MockRemoteDataSource
    ): RemoteDataSource
}