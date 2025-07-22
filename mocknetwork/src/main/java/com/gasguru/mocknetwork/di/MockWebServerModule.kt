package com.gasguru.mocknetwork.di

import com.gasguru.mocknetwork.MockWebServerManager
import com.gasguru.mocknetwork.MockWebServerManagerImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MockWebServerModule {

    @Binds
    abstract fun bindMockServerManager(
        impl: MockWebServerManagerImp
    ): MockWebServerManager
}
