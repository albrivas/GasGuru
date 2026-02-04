package com.gasguru.feature.detail_station.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.gasguru.core.common.CommonUtils.isStationOpen
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.ui.mapper.toPriceUiModel
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.uikit.components.price.PriceItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.feature.detail_station.R

@Composable
fun rememberDetailStationState(station: FuelStationUiModel) = remember(station) {
    DetailStationState(station)
}

@Stable
class DetailStationState(internal val station: FuelStationUiModel) {

    internal val fuelItems: List<PriceItemModel>
        @Composable get() = FuelTypeUiModel.ALL_FUELS.mapNotNull { fuelUiModel ->
            val priceModel = fuelUiModel.type.toPriceUiModel(fuelStation = station.fuelStation)
            if (!priceModel.hasPrice) return@mapNotNull null

            PriceItemModel(
                icon = fuelUiModel.iconRes,
                fuelName = stringResource(id = fuelUiModel.translationRes),
                price = priceModel.formattedPrice
            )
        }

    internal val formattedDistance: String
        get() = station.fuelStation.formatDistance()

    internal val isOpen: Boolean
        get() = station.fuelStation.isStationOpen()

    internal val openCloseText: String
        @Composable get() = if (isOpen) {
            stringResource(id = R.string.open)
        } else {
            stringResource(id = R.string.close)
        }

    internal val formattedName: String
        get() = station.fuelStation.brandStationName.toLowerCase(Locale.current)
            .replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(java.util.Locale.getDefault())
                } else {
                    it.toString()
                }
            }

    internal val colorStationOpen: Color
        @Composable get() = when (isOpen) {
            true -> GasGuruTheme.colors.primary500
            false -> GasGuruTheme.colors.accentRed
        }

    internal val brandIcon: Int
        get() = station.brandIcon

    internal val idServiceStation: Int
        get() = station.fuelStation.idServiceStation

    internal val isFavorite: Boolean
        get() = station.fuelStation.isFavorite

    internal val hasPriceAlert: Boolean
        get() = station.fuelStation.hasPriceAlert

    internal val location: LatLng
        get() = station.fuelStation.location

    internal val schedule: String
        get() = station.fuelStation.schedule

    internal val formattedDirection: String
        get() = station.formattedDirection
}
