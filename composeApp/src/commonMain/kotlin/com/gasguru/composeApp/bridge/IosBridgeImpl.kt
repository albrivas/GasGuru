package com.gasguru.composeApp.bridge

import com.gasguru.analytics.trackStationSyncWorkerRetried
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.domain.fuelstation.GetFuelStationUseCase
import com.gasguru.navigation.deeplink.DeepLinkStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IosBridgeImpl(
    private val deepLinkStateHolder: DeepLinkStateHolder,
    private val analyticsHelper: AnalyticsHelper,
    private val getFuelStationUseCase: GetFuelStationUseCase,
    private val scope: CoroutineScope,
) : IosBridge {

    override fun handlePushTap(stationId: Int) {
        deepLinkStateHolder.setPendingStationId(stationId = stationId)
    }

    override fun refreshStations(onComplete: (Boolean) -> Unit) {
        scope.launch {
            val success = try {
                getFuelStationUseCase.getFuelInAllStations()
                true
            } catch (exception: Exception) {
                analyticsHelper.trackStationSyncWorkerRetried(
                    errorMessage = exception.message.orEmpty(),
                    errorType = exception::class.simpleName.orEmpty(),
                )
                false
            }
            withContext(Dispatchers.Main) { onComplete(success) }
        }
    }
}
