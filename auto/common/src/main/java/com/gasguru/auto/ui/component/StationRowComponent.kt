package com.gasguru.auto.ui.component

import androidx.car.app.CarContext
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.uikit.theme.GasGuruColors

object StationRowComponent {

    private fun PriceCategory.toMarkerColor(theme: GasGuruColors): Color = when (this) {
        PriceCategory.NONE -> theme.secondaryLight
        PriceCategory.CHEAP -> theme.accentGreen
        PriceCategory.NORMAL -> theme.accentOrange
        PriceCategory.EXPENSIVE -> theme.accentRed
    }

    fun createStationRow(
        stationModel: FuelStationUiModel,
        selectedFuel: FuelType?,
        theme: GasGuruColors,
        carContext: CarContext,
        onStationClick: (latitude: Double, longitude: Double) -> Unit
    ): Row {
        val markerColor = stationModel.fuelStation.priceCategory.toMarkerColor(theme)
        return Row.Builder()
            .setTitle(
                "${stationModel.formattedName} - ${
                    selectedFuel?.getPrice(carContext, stationModel.fuelStation) ?: ""
                }"
            )
            .setMetadata(
                Metadata.Builder()
                    .setPlace(
                        Place.Builder(
                            CarLocation.create(
                                stationModel.fuelStation.location.latitude,
                                stationModel.fuelStation.location.longitude
                            )
                        ).setMarker(
                            PlaceMarker.Builder().setColor(
                                CarColor.createCustom(
                                    markerColor.toArgb(),
                                    markerColor.toArgb(),
                                )
                            ).build()
                        )
                            .build()
                    ).build()
            )
            .setBrowsable(true)
            .setOnClickListener {
                onStationClick(
                    stationModel.fuelStation.location.latitude,
                    stationModel.fuelStation.location.longitude
                )
            }
            .addText(stationModel.formattedDirection)
            .addText(stationModel.formattedDistance)
            .build()
    }
}
