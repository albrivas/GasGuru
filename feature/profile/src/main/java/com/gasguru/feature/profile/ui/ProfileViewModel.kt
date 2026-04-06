package com.gasguru.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.feature.profile.analytics.trackVehicleDeleted
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.user.SaveThemeModeUseCase
import com.gasguru.core.domain.vehicle.DeleteVehicleUseCase
import com.gasguru.core.domain.vehicle.GetVehicleByIdUseCase
import com.gasguru.core.domain.vehicle.SaveVehicleUseCase
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.mapper.toUi
import com.gasguru.core.ui.models.ThemeModeUi
import com.gasguru.feature.profile.ui.mapper.toVehicleItemCardModel
import com.gasguru.navigation.manager.NavigationDestination
import com.gasguru.navigation.manager.NavigationManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

class ProfileViewModel(
    getUserData: GetUserDataUseCase,
    private val saveThemeModeUseCase: SaveThemeModeUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase,
    private val saveVehicleUseCase: SaveVehicleUseCase,
    private val navigationManager: NavigationManager,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val allThemesUi by lazy(LazyThreadSafetyMode.NONE) {
        ThemeMode.entries.map { it.toUi() }.toImmutableList()
    }

    val userData: StateFlow<ProfileUiState> = getUserData().map { userData ->
        val vehicles = userData.vehicles
            .sortedByDescending { it.isPrincipal }
            .map { vehicle ->
                vehicle.toVehicleItemCardModel(isSelected = vehicle.isPrincipal)
            }
        ProfileUiState.Success(
            content = ProfileContentUi(
                themeUi = userData.themeMode.toUi(),
                allThemesUi = allThemesUi,
                vehicles = vehicles,
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState.Loading,
    )

    fun handleEvents(event: ProfileEvents) {
        when (event) {
            is ProfileEvents.Theme -> saveTheme(theme = event.theme)
            is ProfileEvents.AddVehicle -> navigationManager.navigateTo(destination = NavigationDestination.AddVehicle)
            is ProfileEvents.EditVehicle -> navigationManager.navigateTo(
                destination = NavigationDestination.EditVehicle(vehicleId = event.vehicleId),
            )

            is ProfileEvents.DeleteVehicle -> deleteVehicle(vehicleId = event.vehicleId)
        }
    }

    private fun saveTheme(theme: ThemeModeUi) = viewModelScope.launch {
        saveThemeModeUseCase(themeMode = theme.mode)
    }

    private fun deleteVehicle(vehicleId: Long) = viewModelScope.launch {
        val currentVehicles =
            (userData.value as? ProfileUiState.Success)?.content?.vehicles ?: return@launch
        if (currentVehicles.size <= 1) return@launch

        val deletedVehicleModel =
            currentVehicles.firstOrNull { it.id == vehicleId } ?: return@launch
        val shouldPromotePrincipal = deletedVehicleModel.isSelected && currentVehicles.size == 2

        analyticsHelper.trackVehicleDeleted(
            wasPrincipal = deletedVehicleModel.isSelected,
            vehiclesRemaining = currentVehicles.size - 1,
        )

        if (shouldPromotePrincipal) {
            val remainingVehicleId = currentVehicles.first { it.id != vehicleId }.id
            val remainingVehicle = getVehicleByIdUseCase(vehicleId = remainingVehicleId)
            if (remainingVehicle != null) {
                saveVehicleUseCase(vehicle = remainingVehicle.copy(isPrincipal = true))
            }
        }

        deleteVehicleUseCase(vehicleId = vehicleId)
    }
}
