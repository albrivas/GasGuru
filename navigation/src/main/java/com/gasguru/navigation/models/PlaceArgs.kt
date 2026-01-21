package com.gasguru.navigation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceArgs(
    val id: String,
    val name: String
) : Parcelable
