package com.gasguru.core.data.repository.geocoder

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import com.gasguru.core.common.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class GeocoderAddressImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : GeocoderAddress {
    @OptIn(FlowPreview::class)
    override fun getAddressFromLocation(latitude: Double, longitude: Double): Flow<String?> = flow {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            callbackFlow {
                val listener = object : GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        trySend(addresses.firstOrNull()?.getAddressLine(0))
                        close()
                    }

                    override fun onError(errorMessage: String?) {
                        trySend(null)
                        close(IOException(errorMessage))
                    }
                }
                geocoder.getFromLocation(latitude, longitude, 1, listener)
                awaitClose { }
            }.collect { emit(it) }
        } else {
            val addresses = withContext(ioDispatcher) {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)
            }
            emit(addresses?.firstOrNull()?.getAddressLine(0))
        }
    }.flowOn(ioDispatcher)
}
