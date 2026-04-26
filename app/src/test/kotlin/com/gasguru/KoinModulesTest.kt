package com.gasguru

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.gasguru.core.analytics.di.analyticsModule
import com.gasguru.core.common.coroutineModule
import com.gasguru.core.components.searchbar.GasGuruSearchBarViewModel
import com.gasguru.core.components.searchbar.di.searchBarModule
import com.gasguru.core.data.di.androidDataModule
import com.gasguru.core.data.di.commonDataModule
import com.gasguru.core.data.di.dataProviderModule
import com.gasguru.core.database.di.daoModule
import com.gasguru.core.database.di.databaseModule
import com.gasguru.core.domain.di.domainModule
import com.gasguru.core.network.di.ktorModule
import com.gasguru.core.network.di.placesModule
import com.gasguru.core.notifications.di.notificationModule
import com.gasguru.core.supabase.di.supabaseModule
import com.gasguru.di.appModule
import com.gasguru.di.remoteDataSourceModule
import com.gasguru.feature.detail_station.di.detailStationModule
import com.gasguru.feature.detail_station.ui.DetailStationViewModel
import com.gasguru.feature.favorite_list_station.di.favoriteListStationModule
import com.gasguru.feature.onboarding_welcome.di.onboardingModule
import com.gasguru.feature.profile.di.profileModule
import com.gasguru.feature.route_planner.di.routePlannerModule
import com.gasguru.feature.station_map.di.stationMapModule
import com.gasguru.feature.vehicle.di.vehicleModule
import com.gasguru.feature.vehicle.viewmodel.AddVehicleViewModel
import com.gasguru.navigation.di.navigationModule
import com.mixpanel.android.mpmetrics.MixpanelAPI
import io.ktor.client.engine.HttpClientEngine
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class KoinModulesTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    @DisplayName(
        """
        GIVEN all registered Koin modules
        WHEN verifying the dependency graph
        THEN all dependencies are satisfied without missing bindings
        """
    )
    fun verifyKoinGraph() {
        module {
            includes(
                analyticsModule,
                coroutineModule,
                databaseModule,
                daoModule,
                ktorModule,
                placesModule(),
                supabaseModule,
                notificationModule,
                commonDataModule,
                androidDataModule,
                dataProviderModule,
                domainModule(),
                navigationModule(),
                remoteDataSourceModule(),
                appModule(),
                stationMapModule(),
                detailStationModule(),
                favoriteListStationModule(),
                profileModule(),
                routePlannerModule(),
                onboardingModule(),
                vehicleModule(),
                searchBarModule(),
            )
        }.verify(
            extraTypes = listOf(
                Context::class,
                MixpanelAPI::class,
                HttpClientEngine::class,
            ),
            injections = injectedParameters(
                definition<DetailStationViewModel>(SavedStateHandle::class),
                definition<AddVehicleViewModel>(SavedStateHandle::class),
                definition<GasGuruSearchBarViewModel>(SavedStateHandle::class),
            ),
        )
    }
}
