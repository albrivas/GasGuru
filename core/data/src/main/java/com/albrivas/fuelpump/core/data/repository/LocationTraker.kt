package com.albrivas.fuelpump.core.data.repository

import android.location.Location

fun interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}