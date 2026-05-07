package com.gasguru.feature.detail_station.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.ui.mapper.toPriceUiModel
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.uikit.components.price.PriceItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.feature.detail_station.generated.resources.Res
import com.gasguru.feature.detail_station.generated.resources.close
import com.gasguru.feature.detail_station.generated.resources.open
import com.gasguru.feature.detail_station.generated.resources.share_download
import com.gasguru.feature.detail_station.generated.resources.share_fuel_prices
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun rememberDetailStationState(station: FuelStationUiModel, isOpen: Boolean) = remember(station, isOpen) {
    DetailStationState(station = station, isOpen = isOpen)
}

@Stable
class DetailStationState(internal val station: FuelStationUiModel, internal val isOpen: Boolean) {

    internal val fuelItems: List<PriceItemModel>
        @Composable get() = FuelTypeUiModel.ALL_FUELS.mapNotNull { fuelUiModel ->
            val priceModel = fuelUiModel.type.toPriceUiModel(fuelStation = station.fuelStation)
            if (!priceModel.hasPrice) return@mapNotNull null

            PriceItemModel(
                icon = fuelUiModel.iconRes,
                fuelName = stringResource(fuelUiModel.translationRes),
                price = priceModel.formattedPrice,
            )
        }

    internal val formattedDistance: String
        get() = station.fuelStation.formatDistance()

    internal val openCloseText: String
        @Composable get() = if (isOpen) {
            stringResource(Res.string.open)
        } else {
            stringResource(Res.string.close)
        }

    internal val formattedName: String
        get() = station.fuelStation.brandStationName.toLowerCase(Locale.current)
            .replaceFirstChar { it.uppercase() }

    internal val colorStationOpen: Color
        @Composable get() = when (isOpen) {
            true -> GasGuruTheme.colors.primary500
            false -> GasGuruTheme.colors.accentRed
        }

    internal val brandIcon: DrawableResource
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

    @Composable
    internal fun buildShareText(address: String?): String {
        val displayAddress = address ?: formattedDirection
        val mapsUrl = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
        val playStoreUrl = "https://play.google.com/store/apps/details?id=com.gasguru"
        val fuelPricesLabel = stringResource(Res.string.share_fuel_prices)
        val fuelPricesText = fuelItems.joinToString(separator = "\n") {
            "  ${it.fuelName}: ${it.price}"
        }
        val downloadText = stringResource(Res.string.share_download)

        return buildString {
            appendLine("⛽ $formattedName")
            appendLine("📍 $displayAddress")
            appendLine("🗺️ $mapsUrl")
            appendLine()
            appendLine("💰 $fuelPricesLabel:")
            appendLine(fuelPricesText)
            appendLine()
            appendLine("📲 $downloadText:")
            append(playStoreUrl)
        }
    }
}
