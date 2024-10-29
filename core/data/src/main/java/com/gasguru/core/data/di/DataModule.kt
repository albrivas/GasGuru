package com.gasguru.core.data.di

import com.gasguru.core.data.repository.FuelStationRepository
import com.gasguru.core.data.repository.LocationTracker
import com.gasguru.core.data.repository.LocationTrackerRepository
import com.gasguru.core.data.repository.OfflineFuelStationRepository
import com.gasguru.core.data.repository.OfflineRecentSearchRepository
import com.gasguru.core.data.repository.OfflineRecentSearchRepositoryImp
import com.gasguru.core.data.repository.OfflineUserDataRepository
import com.gasguru.core.data.repository.PlacesRepository
import com.gasguru.core.data.repository.PlacesRepositoryImp
import com.gasguru.core.data.repository.UserDataRepository
import com.gasguru.core.network.datasource.PlacesDataSource
import com.gasguru.core.network.datasource.PlacesDataSourceImp
import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.datasource.RemoteDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsFuelStationRepository(
        fuelStationRepository: OfflineFuelStationRepository
    ): FuelStationRepository

    @Binds
    fun bindRemoteDataSourceImp(
        remoteDataSource: RemoteDataSourceImp
    ): RemoteDataSource

    @Binds
    fun bindUserDataRepository(
        userDataRepository: OfflineUserDataRepository
    ): UserDataRepository

    @Binds
    fun bindLocationTrackerRepository(
        locationTrackerRepository: LocationTrackerRepository
    ): LocationTracker

    @Binds
    fun bindPlacesRepository(
        placesRepository: PlacesRepositoryImp
    ): PlacesRepository

    @Binds
    fun bindPlacesDataSource(
        placesDataSource: PlacesDataSourceImp
    ): PlacesDataSource

    @Binds
    fun bindRecentSearchRepository(
        recentSearchRepository: OfflineRecentSearchRepositoryImp
    ): OfflineRecentSearchRepository
}
