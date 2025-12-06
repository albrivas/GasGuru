package com.gasguru.core.notifications.di

import com.gasguru.core.notifications.OneSignalManager
import com.gasguru.core.notifications.OneSignalManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NotificationModule {

    @Binds
    fun bindOneSignalManager(
        oneSignalManagerImpl: OneSignalManagerImpl,
    ): OneSignalManager
}
