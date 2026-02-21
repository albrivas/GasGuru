package com.gasguru.core.notifications.di

import com.gasguru.core.notifications.OneSignalManager
import com.gasguru.core.notifications.OneSignalManagerImpl
import com.gasguru.core.notifications.PushNotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {
    single<OneSignalManager> { OneSignalManagerImpl() }
    single { PushNotificationService(context = androidContext()) }
}
