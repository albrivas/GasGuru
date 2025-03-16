package com.gasguru.auto.di

import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CarScreenEntryPoint {
    fun getCurrentLocationUseCase(): GetCurrentLocationUseCase
}