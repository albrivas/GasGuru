package com.gasguru.core.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationTrackerRepository @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
) : LocationTracker {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? =
        locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()

    override suspend fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override val getCurrentLocationFlow: Flow<Location?>
        @SuppressLint("MissingPermission")
        get() = flow {
            try {
                val location = locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).await()
                emit(location)
            } catch (e: SecurityException) {
                Log.e("LocationTracker", "Security exception in getCurrentLocationFlow", e)
                emit(null)
            } catch (e: ApiException) {
                Log.e("LocationTracker", "API exception in getCurrentLocationFlow", e)
                emit(null)
            }
        }
    override val getLastKnownLocation: Flow<Location?>
        @SuppressLint("MissingPermission")
        get() = flow {
            try {
                val location = locationClient.lastLocation.await()
                emit(location)
            } catch (e: SecurityException) {
                Log.e("LocationTracker", "Security exception in getLasKnownLocation", e)
                emit(null)
            } catch (e: ApiException) {
                Log.e("LocationTracker", "API exception in getLasKnownLocation", e)
                emit(null)
            }
        }
}
