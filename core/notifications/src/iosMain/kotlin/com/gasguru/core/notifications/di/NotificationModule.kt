package com.gasguru.core.notifications.di

import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.notifications.NotificationService
import com.gasguru.core.notifications.OneSignalManager
import com.gasguru.core.notifications.OneSignalManagerIos
import com.gasguru.core.notifications.PushNotificationServiceIos
import org.koin.dsl.module

val notificationModule = module {
    single<OneSignalManager> { OneSignalManagerIos() }
    single<NotificationService> {
        PushNotificationServiceIos(analyticsHelper = get<AnalyticsHelper>())
    }
}
