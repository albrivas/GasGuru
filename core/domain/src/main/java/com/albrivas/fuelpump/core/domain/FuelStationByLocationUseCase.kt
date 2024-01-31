package com.albrivas.fuelpump.core.domain

import android.location.Location
import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import com.albrivas.fuelpump.core.model.data.FuelType
import javax.inject.Inject

class FuelStationByLocationUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository
) {
    operator fun invoke(userLocation: Location) =
        repository.getFuelStationByLocation(userLocation)
}