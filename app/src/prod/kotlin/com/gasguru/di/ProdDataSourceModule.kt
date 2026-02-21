package com.gasguru.di

import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.datasource.RemoteDataSourceImp
import org.koin.dsl.module

val remoteDataSourceModule = module {
    single<RemoteDataSource> { RemoteDataSourceImp(api = get()) }
}
