package com.gasguru.core.uikit.components.price

import org.jetbrains.compose.resources.DrawableResource

data class PriceItemModel(
    val icon: DrawableResource,
    val price: String,
    val fuelName: String,
)
