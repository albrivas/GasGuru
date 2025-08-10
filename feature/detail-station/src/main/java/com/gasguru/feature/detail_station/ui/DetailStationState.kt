package com.gasguru.feature.detail_station.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.gasguru.core.common.CommonUtils.isStationOpen
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.ui.getFuelPriceItems
import com.gasguru.core.uikit.components.price.PriceItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.feature.detail_station.R

@Composable
fun rememberDetailStationState(station: FuelStation) = remember(station) { 
    DetailStationState(station) 
}

@Stable
class DetailStationState(private val station: FuelStation) {

    internal val fuelItems: List<PriceItemModel>
        @Composable get() = station.getFuelPriceItems()
    
    internal val formattedDistance: String
        get() = station.formatDistance()
    
    internal val isOpen: Boolean
        get() = station.isStationOpen()
    
    internal val openCloseText: String
        @Composable get() = if (isOpen) {
            stringResource(id = R.string.open)
        } else {
            stringResource(id = R.string.close)
        }
    
    internal val formattedName: String
        get() = station.brandStationName.toLowerCase(Locale.current)
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
}