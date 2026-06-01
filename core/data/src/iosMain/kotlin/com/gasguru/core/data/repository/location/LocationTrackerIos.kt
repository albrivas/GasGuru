package com.gasguru.core.data.repository.location

import com.gasguru.core.model.data.LatLng
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
class LocationTrackerIos(
    private val ioDispatcher: CoroutineDispatcher,
) : LocationTracker {

    override suspend fun getCurrentLocation(): LatLng? = withContext(Dispatchers.Main) {
        val manager = CLLocationManager()
        suspendCancellableCoroutine { continuation ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>,
                ) {
                    @Suppress("UNCHECKED_CAST")
                    val location = (didUpdateLocations as? List<CLLocation>)?.lastOrNull()
                    continuation.resume(location?.toLatLng())
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError,
                ) {
                    continuation.resume(null)
                }
            }
            manager.delegate = delegate
            manager.requestLocation()
            continuation.invokeOnCancellation {
                manager.delegate = null
                manager.stopUpdatingLocation()
            }
        }
    }

    override val isLocationEnabled: Flow<Boolean> = callbackFlow {
        val manager = CLLocationManager()
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                trySend(manager.isLocationAuthorized())
            }
        }
        manager.delegate = delegate
        trySend(
            CLLocationManager.locationServicesEnabled() && manager.isLocationAuthorized(),
        )
        awaitClose { manager.delegate = null }
    }

    override val getCurrentLocationFlow: Flow<LatLng?> = callbackFlow {
        val manager = CLLocationManager()
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(
                manager: CLLocationManager,
                didUpdateLocations: List<*>,
            ) {
                @Suppress("UNCHECKED_CAST")
                val location = (didUpdateLocations as? List<CLLocation>)?.lastOrNull()
                trySend(location?.toLatLng())
            }
        }
        manager.delegate = delegate
        manager.startUpdatingLocation()
        awaitClose {
            manager.stopUpdatingLocation()
            manager.delegate = null
        }
    }

    override val getLastKnownLocation: Flow<LatLng?> = flow {
        val manager = CLLocationManager()
        emit(manager.location?.toLatLng())
    }
}

private fun CLLocationManager.isLocationAuthorized(): Boolean =
    authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
        authorizationStatus == kCLAuthorizationStatusAuthorizedAlways

@OptIn(ExperimentalForeignApi::class)
private fun CLLocation.toLatLng(): LatLng =
    coordinate.useContents { LatLng(latitude = latitude, longitude = longitude) }
