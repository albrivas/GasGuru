package com.gasguru.core.notifications.di

import com.gasguru.core.notifications.NotificationsSecrets
import com.gasguru.core.notifications.OneSignalManager
import org.koin.dsl.module

fun provideNotificationModuleIos(oneSignalManager: OneSignalManager) = module {
    single<OneSignalManager> { oneSignalManager }
}

fun getOneSignalAppId(): String = NotificationsSecrets.ONESIGNAL_APP_ID
