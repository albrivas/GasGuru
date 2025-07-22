package com.gasguru.di

import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.datasource.RemoteDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProdDataSourceModule {

    @Binds
    abstract fun bindRemoteDataSourceImpl(
        remoteDataSourceImpl: RemoteDataSourceImp
    ): RemoteDataSource
}