package com.gasguru.core.model.data

import android.location.Location

data class Route(
    val legs: List<String>,
    val steps: List<Location>
)
