package com.gasguru.core.ui.sort

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelStationUiModel
import kotlin.jvm.JvmName

/**
 * Ordena la lista según [criteria], usando el precio del [fuelType] seleccionado por el usuario.
 *
 * Función pura, sin efectos secundarios ni dependencias de Koin: puede llamarse tanto desde un
 * ViewModel como desde una `Screen` de Android Auto.
 */
fun List<FuelStationUiModel>.sortedByCriteria(
    criteria: StationSortCriteria,
    fuelType: FuelType,
): List<FuelStationUiModel> = when (criteria) {
    StationSortCriteria.PRICE -> sortedBy { stationUiModel -> fuelType.extractPrice(stationUiModel.fuelStation) }
    StationSortCriteria.DISTANCE -> sortedBy { stationUiModel -> stationUiModel.fuelStation.distance }
    StationSortCriteria.NONE -> this
}

/**
 * Sobrecarga sobre [FuelStation] para consumidores que aún no tienen [FuelStationUiModel]
 * (por ejemplo, el widget de favoritos).
 */
@JvmName("sortedFuelStationsByCriteria")
fun List<FuelStation>.sortedByCriteria(
    criteria: StationSortCriteria,
    fuelType: FuelType,
): List<FuelStation> = when (criteria) {
    StationSortCriteria.PRICE -> sortedBy { fuelStation -> fuelType.extractPrice(fuelStation) }
    StationSortCriteria.DISTANCE -> sortedBy { fuelStation -> fuelStation.distance }
    StationSortCriteria.NONE -> this
}
