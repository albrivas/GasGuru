package com.gasguru.core.domain.notifications

import com.gasguru.core.notifications.OneSignalManager
import javax.inject.Inject

class SetNotificationTagUseCase @Inject constructor(
    private val oneSignalManager: OneSignalManager,
) {
    suspend operator fun invoke(enable: Boolean) =
        oneSignalManager.enablePriceNotificationAlert(enable = enable)
}