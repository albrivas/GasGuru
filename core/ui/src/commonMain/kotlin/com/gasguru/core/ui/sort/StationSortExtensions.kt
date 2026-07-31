package com.gasguru.core.ui.sort

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelStationUiModel
import kotlin.jvm.JvmName

fun List<FuelStationUiModel>.sortedByCriteria(
    criteria: StationSortCriteria,
    fuelType: FuelType,
): List<FuelStationUiModel> = when (criteria) {
    StationSortCriteria.PRICE -> sortedBy { stationUiModel -> fuelType.extractPrice(stationUiModel.fuelStation) }
    StationSortCriteria.DISTANCE -> sortedBy { stationUiModel -> stationUiModel.fuelStation.distance }
    StationSortCriteria.NONE -> this
}

// JvmName avoids a signature clash with the FuelStationUiModel overload after type erasure.
@JvmName("sortedFuelStationsByCriteria")
fun List<FuelStation>.sortedByCriteria(
    criteria: StationSortCriteria,
    fuelType: FuelType,
): List<FuelStation> = when (criteria) {
    StationSortCriteria.PRICE -> sortedBy { fuelStation -> fuelType.extractPrice(fuelStation) }
    StationSortCriteria.DISTANCE -> sortedBy { fuelStation -> fuelStation.distance }
    StationSortCriteria.NONE -> this
}
