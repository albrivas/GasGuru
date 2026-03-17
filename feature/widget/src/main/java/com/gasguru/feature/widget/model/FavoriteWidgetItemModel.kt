package com.gasguru.feature.widget.model

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory

data class FavoriteWidgetItemModel(
    val idServiceStation: Int,
    val name: String,
    val direction: String,
    val formattedPrice: String,
    val priceCategory: PriceCategory,
)

fun FuelStation.toWidgetItemModel(fuelType: FuelType): FavoriteWidgetItemModel {
    val price = fuelType.extractPrice(this)
    val formattedPrice = if (price > 0.0) "%.3f €".format(price) else "-"
    return FavoriteWidgetItemModel(
        idServiceStation = idServiceStation,
        name = formatName(),
        direction = formatDirection(),
        formattedPrice = formattedPrice,
        priceCategory = priceCategory,
    )
}
