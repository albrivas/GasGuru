package com.gasguru.navigation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoutePlanArgs(
    val originId: String?,
    val destinationId: String?,
    val destinationName: String?,
) : Parcelable
