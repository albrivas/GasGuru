package com.albrivas.fuelpump.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.domain.GetFuelStationUseCase
import com.albrivas.fuelpump.core.domain.model.FuelStationDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getListFuelStation: GetFuelStationUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            getListFuelStation.temporalFillBBDD()
            val result = getListFuelStation()
            result.collect { stations ->
                //TODO: show information stations in list
            }
        }
    }
}

sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Error(val throwable: Throwable) : TaskUiState
    data class Success(val data: List<FuelStationDomain>) : TaskUiState
}
