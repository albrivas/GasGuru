package com.gasguru.navigation.models

import kotlinx.serialization.Serializable

@Serializable
data class RoutePlanArgs(
    val originId: String?,
    val destinationId: String?,
    val destinationName: String?,
)
