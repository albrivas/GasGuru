package com.gasguru.navigation.models

import kotlinx.serialization.Serializable

@Serializable
data class PlaceArgs(
    val id: String,
    val name: String,
)
