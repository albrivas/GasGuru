package com.gasguru.core.data.di

import com.gasguru.core.data.repository.filter.FilterRepository
import com.gasguru.core.data.repository.filter.FilterRepositoryImpl
import com.gasguru.core.data.repository.geocoder.GeocoderAddress
import com.gasguru.core.data.repository.geocoder.GeocoderAddressImpl
import com.gasguru.core.data.repository.location.LocationTracker
import com.gasguru.core.data.repository.location.LocationTrackerRepository
import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.data.repository.places.PlacesRepositoryImp
import com.gasguru.core.data.repository.route.RoutesRepository
import com.gasguru.core.data.repository.route.RoutesRepositoryImpl
import com.gasguru.core.data.repository.search.OfflineRecentSearchRepository
import com.gasguru.core.data.repository.search.OfflineRecentSearchRepositoryImp
import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.data.repository.user.OfflineUserDataRepository
import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.data.util.ConnectivityManagerNetworkMonitor
import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.network.datasource.PlacesDataSource
import com.gasguru.core.network.datasource.PlacesDataSourceImp
import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.datasource.RemoteDataSourceImp
import com.gasguru.core.network.datasource.RoutesDataSource
import com.gasguru.core.network.datasource.RoutesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsFuelStationRepository(
        fuelStationRepository: OfflineFuelStationRepository,
    ): FuelStationRepository

    @Binds
    fun bindRemoteDataSourceImp(
        remoteDataSource: RemoteDataSourceImp,
    ): RemoteDataSource

    @Binds
    fun bindRouteDataSourceImp(
        routesDataSource: RoutesDataSourceImpl
    ): RoutesDataSource

    @Binds
    fun bindUserDataRepository(
        userDataRepository: OfflineUserDataRepository,
    ): UserDataRepository

    @Binds
    fun bindLocationTrackerRepository(
        locationTrackerRepository: LocationTrackerRepository,
    ): LocationTracker

    @Binds
    fun bindPlacesRepository(
        placesRepository: PlacesRepositoryImp,
    ): PlacesRepository

    @Binds
    fun bindPlacesDataSource(
        placesDataSource: PlacesDataSourceImp,
    ): PlacesDataSource

    @Binds
    fun bindRecentSearchRepository(
        recentSearchRepository: OfflineRecentSearchRepositoryImp,
    ): OfflineRecentSearchRepository

    @Binds
    fun bindGeocoderAddress(
        geocoderAddress: GeocoderAddressImpl,
    ): GeocoderAddress

    @Binds
    fun bindFilterRepository(
        filterRepository: FilterRepositoryImpl,
    ): FilterRepository

    @Binds
    fun bindConnectivityManager(
        connectivityManagerNetworkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    fun bindRoutesRepository(
        routesRepository: RoutesRepositoryImpl
    ): RoutesRepository
}
