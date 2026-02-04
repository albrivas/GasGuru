package com.gasguru.core.ui.mapper

import androidx.compose.runtime.Composable
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.toColor
import com.gasguru.core.uikit.components.station_list.StationListItemModel

@Composable
fun List<FuelStationUiModel>.toStationListItems(selectedFuel: FuelType): List<StationListItemModel> {
    return map {
        StationListItemModel(
            idServiceStation = it.fuelStation.idServiceStation,
            icon = it.brandIcon,
            name = it.formattedName,
            distance = it.formattedDistance,
            price = selectedFuel.getPrice(it.fuelStation),
            categoryColor = it.fuelStation.priceCategory.toColor()
        )
    }
}
