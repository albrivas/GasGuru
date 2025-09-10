package com.gasguru.auto.ui.component

import androidx.car.app.CarContext
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.compose.ui.graphics.toArgb
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.getPrice
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.uikit.theme.GasGuruColors

object StationRowComponent {

    fun createStationRow(
        stationModel: FuelStationUiModel,
        selectedFuel: FuelType?,
        theme: GasGuruColors,
        carContext: CarContext,
        onStationClick: (latitude: Double, longitude: Double) -> Unit
    ): Row {
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
                                    theme.neutralWhite.toArgb(),
                                    theme.primary600.toArgb(),
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
