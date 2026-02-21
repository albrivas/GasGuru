package com.gasguru.navigation.di

import com.gasguru.navigation.deeplink.DeepLinkStateHolder
import com.gasguru.navigation.manager.NavigationManager
import com.gasguru.navigation.manager.NavigationManagerImpl
import org.koin.dsl.module

val navigationModule = module {
    single<NavigationManager> { NavigationManagerImpl() }
    single { DeepLinkStateHolder() }
}
