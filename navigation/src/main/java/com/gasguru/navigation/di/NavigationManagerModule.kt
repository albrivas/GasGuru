package com.gasguru.navigation.di

import com.gasguru.navigation.manager.NavigationManager
import com.gasguru.navigation.manager.NavigationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationManagerModule {

    @Binds
    @Singleton
    abstract fun bindNavigationManager(
        navigationManagerImpl: NavigationManagerImpl,
    ): NavigationManager
}