package com.gasguru.core.domain.di

import com.gasguru.core.domain.alerts.AddPriceAlertUseCase
import com.gasguru.core.domain.alerts.RemovePriceAlertUseCase
import com.gasguru.core.domain.connectivity.ConnectivityNetworkUseCase
import com.gasguru.core.domain.filters.GetFiltersUseCase
import com.gasguru.core.domain.filters.SaveFilterUseCase
import com.gasguru.core.domain.fuelstation.FuelStationByLocationUseCase
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationByIdUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationsInRouteUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.fuelstation.SaveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetCurrentLocationFlowUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.location.IsLocationEnabledUseCase
import com.gasguru.core.domain.maps.GetStaticMapUrlUseCase
import com.gasguru.core.domain.places.GetAddressFromLocationUseCase
import com.gasguru.core.domain.places.GetLocationPlaceUseCase
import com.gasguru.core.domain.places.GetPlacesUseCase
import com.gasguru.core.domain.route.GetRouteUseCase
import com.gasguru.core.domain.search.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.search.GetRecentSearchQueryUseCase
import com.gasguru.core.domain.search.InsertRecentSearchQueryUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.user.SaveThemeModeUseCase
import com.gasguru.core.domain.vehicle.GetVehiclesUseCase
import com.gasguru.core.domain.vehicle.SaveDefaultVehicleCapacityUseCase
import com.gasguru.core.domain.vehicle.SaveDefaultVehicleFuelTypeUseCase
import com.gasguru.core.domain.vehicle.UpdateVehicleFuelTypeUseCase
import com.gasguru.core.domain.vehicle.UpdateVehicleTankCapacityUseCase
import org.koin.dsl.module

val domainModule = module {
    // Alerts
    factory { AddPriceAlertUseCase(priceAlertRepository = get()) }
    factory { RemovePriceAlertUseCase(priceAlertRepository = get()) }

    // Connectivity
    factory { ConnectivityNetworkUseCase(networkMonitor = get()) }

    // Filters
    factory { GetFiltersUseCase(filterRepository = get()) }
    factory { SaveFilterUseCase(filterRepository = get()) }

    // Fuel stations
    factory { FuelStationByLocationUseCase(repository = get()) }
    factory { GetFuelStationByIdUseCase(repository = get()) }
    factory { GetFuelStationUseCase(repository = get()) }
    factory { GetFuelStationsInRouteUseCase(repository = get()) }
    factory { GetFavoriteStationsUseCase(repository = get()) }
    factory { SaveFavoriteStationUseCase(offlineRepository = get()) }
    factory { RemoveFavoriteStationUseCase(offlineRepository = get()) }
    // Location
    factory { GetCurrentLocationUseCase(locationTracker = get()) }
    factory { GetCurrentLocationFlowUseCase(locationTracker = get()) }
    factory { GetLastKnownLocationUseCase(locationTracker = get()) }
    factory { IsLocationEnabledUseCase(locationTracker = get()) }

    // Maps
    factory { GetStaticMapUrlUseCase(staticMapRepository = get()) }

    // Places
    factory { GetAddressFromLocationUseCase(geocoderAddress = get()) }
    factory { GetLocationPlaceUseCase(placesRepository = get()) }
    factory { GetPlacesUseCase(placesRepository = get()) }

    // Routes
    factory { GetRouteUseCase(routesRepository = get()) }

    // Search
    factory { ClearRecentSearchQueriesUseCase(recentSearchRepository = get()) }
    factory { GetRecentSearchQueryUseCase(recentSearchRepository = get()) }
    factory { InsertRecentSearchQueryUseCase(recentSearchRepository = get()) }

    // User
    factory { GetUserDataUseCase(userDataRepository = get()) }
    factory { SaveThemeModeUseCase(userDataRepository = get()) }

    // Vehicle
    factory { GetVehiclesUseCase(vehicleRepository = get()) }
    factory { UpdateVehicleTankCapacityUseCase(vehicleRepository = get()) }
    factory { UpdateVehicleFuelTypeUseCase(vehicleRepository = get()) }
    factory { SaveDefaultVehicleFuelTypeUseCase(vehicleRepository = get()) }
    factory {
        SaveDefaultVehicleCapacityUseCase(
            vehicleRepository = get(),
            userDataRepository = get(),
        )
    }
}
