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
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLLocationAccuracyHundredMeters
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
class LocationTrackerIos(
    private val ioDispatcher: CoroutineDispatcher,
) : LocationTracker {

    // requestLocation() is a one-shot request — more reliable than startUpdatingLocation()
    // right after a fresh permission grant, where continuous updates may be slow to start.
    // CLLocationManager must be created and receive callbacks on the main thread.
    override suspend fun getCurrentLocation(): LatLng? = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val manager = CLLocationManager()
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>,
                ) {
                    @Suppress("UNCHECKED_CAST")
                    val location = (didUpdateLocations as? List<CLLocation>)?.lastOrNull()
                    if (location != null && continuation.isActive) {
                        continuation.resume(location.toLatLng())
                    }
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError,
                ) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
            manager.delegate = delegate
            manager.desiredAccuracy = kCLLocationAccuracyHundredMeters
            manager.requestLocation()
            continuation.invokeOnCancellation { manager.delegate = null }
        }
    }

    // isLocationEnabled mirrors Android behavior: false only when system GPS is OFF or
    // permission is explicitly Denied/Restricted. NotDetermined is treated as "enabled"
    // so that StationMapScreen handles the first-time permission request via
    // requestWhenInUseAuthorization(), not the global GasGuruApp "go to Settings" dialog.
    override val isLocationEnabled: Flow<Boolean> = callbackFlow {
        val manager = CLLocationManager()
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                trySend(CLLocationManager.locationServicesEnabled() && !manager.isLocationDenied())
            }
        }
        manager.delegate = delegate
        trySend(CLLocationManager.locationServicesEnabled() && !manager.isLocationDenied())
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

private fun CLLocationManager.isLocationDenied(): Boolean =
    authorizationStatus == kCLAuthorizationStatusDenied ||
        authorizationStatus == kCLAuthorizationStatusRestricted

@OptIn(ExperimentalForeignApi::class)
private fun CLLocation.toLatLng(): LatLng =
    coordinate.useContents { LatLng(latitude = latitude, longitude = longitude) }
