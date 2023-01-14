package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import com.albrivas.fuelpump.core.model.FuelStation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFuelStationUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository
) {
    operator fun invoke(): Flow<List<FuelStation>> = repository.listFuelStation
}