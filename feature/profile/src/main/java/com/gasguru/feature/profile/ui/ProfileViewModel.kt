package com.gasguru.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.SaveFuelSelectionUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.user.SaveThemeModeUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.models.ThemeModeUi
import com.gasguru.core.ui.models.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserData: GetUserDataUseCase,
    private val saveFuelSelectionUseCase: SaveFuelSelectionUseCase,
    private val saveThemeModeUseCase: SaveThemeModeUseCase,
) : ViewModel() {

    private val allThemesUi by lazy(LazyThreadSafetyMode.NONE) {
        ThemeMode.entries.map { it.toUi() }.toImmutableList()
    }

    val userData: StateFlow<ProfileUiState> = getUserData().map { userData ->
        ProfileUiState.Success(
            content = ProfileContentUi(
                fuelTranslation = FuelTypeUiModel.fromFuelType(userData.fuelSelection).translationRes,
                themeUi = userData.themeMode.toUi(),
                allThemesUi = allThemesUi
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState.Loading
    )

    fun handleEvents(event: ProfileEvents) {
        when (event) {
            is ProfileEvents.Fuel -> saveSelectionFuel(fuelType = event.fuel)
            is ProfileEvents.Theme -> saveTheme(theme = event.theme)
        }
    }

    private fun saveSelectionFuel(fuelType: FuelType) = viewModelScope.launch {
        saveFuelSelectionUseCase(fuelType = fuelType)
    }

    private fun saveTheme(theme: ThemeModeUi) = viewModelScope.launch {
        saveThemeModeUseCase(themeMode = theme.mode)
    }
}
