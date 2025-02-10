package com.gasguru.core.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    @SuppressLint("MissingPermission")
    override val isLocationEnabled: Flow<Boolean> = callbackFlow {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        trySend(
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        )

        val locationListener = object : LocationListener {
            override fun onProviderEnabled(provider: String) {
                trySend(true)
            }

            override fun onProviderDisabled(provider: String) {
                trySend(false)
            }

            override fun onLocationChanged(location: Location) {}
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L,
            0f,
            locationListener
        )

        awaitClose {
            locationManager.removeUpdates(locationListener)
        }
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
                val lastKnownLocation = locationClient.lastLocation.await()
                if (lastKnownLocation != null) {
                    emit(lastKnownLocation)
                } else {
                    emit(getCurrentLocation())
                }
            } catch (e: SecurityException) {
                Log.e("LocationTracker", "Security exception in getLastKnownLocation", e)
                emit(null)
            } catch (e: ApiException) {
                Log.e("LocationTracker", "API exception in getLastKnownLocation", e)
                emit(null)
            }
        }
}
