package com.gasguru.core.data.repository.location

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.gasguru.core.model.data.LatLng
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
    override suspend fun getCurrentLocation(): LatLng? =
        locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()?.toDomainLatLng()

    override val isLocationEnabled: Flow<Boolean> = callbackFlow {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        fun isEnabled() =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        trySend(isEnabled())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(isEnabled())
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
        )

        awaitClose { context.unregisterReceiver(receiver) }
    }

    override val getCurrentLocationFlow: Flow<LatLng?>
        @SuppressLint("MissingPermission")
        get() = flow {
            try {
                val location = locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).await()?.toDomainLatLng()
                emit(location)
            } catch (e: SecurityException) {
                Log.e("LocationTracker", "Security exception in getCurrentLocationFlow", e)
                emit(null)
            } catch (e: ApiException) {
                Log.e("LocationTracker", "API exception in getCurrentLocationFlow", e)
                emit(null)
            }
        }
    override val getLastKnownLocation: Flow<LatLng?>
        @SuppressLint("MissingPermission")
        get() = flow {
            try {
                val lastKnownLocation = locationClient.lastLocation.await()?.toDomainLatLng()
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

fun Location.toDomainLatLng(): LatLng =
    LatLng(latitude = latitude, longitude = longitude)
