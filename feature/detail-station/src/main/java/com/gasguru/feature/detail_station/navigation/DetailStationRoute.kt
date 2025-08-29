package com.gasguru.feature.detail_station.navigation

import kotlinx.serialization.Serializable

@Serializable
data class DetailStationRoute(val idServiceStation: Int)

@Serializable
data class DetailStationDialogRoute(val idServiceStation: Int)
