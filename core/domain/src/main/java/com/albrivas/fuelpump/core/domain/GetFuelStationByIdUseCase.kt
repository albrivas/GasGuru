package com.albrivas.fuelpump.core.domain

import android.location.Location
import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import javax.inject.Inject

class GetFuelStationByIdUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository,
) {
    operator fun invoke(id: Int, userLocation: Location) =
        repository.getFuelStationById(id, userLocation)
}
