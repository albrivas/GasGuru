package com.albrivas.fuelpump.core.data.repository

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
    suspend fun isLocationEnabled(): Boolean
}