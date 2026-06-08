package com.gasguru.feature.detail_station.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter

@Composable
actual fun rememberNotificationPermissionRequester(
    onPermissionGranted: () -> Unit,
): () -> Unit {
    val scope = rememberCoroutineScope()
    return remember(onPermissionGranted) {
        {
            val center = UNUserNotificationCenter.currentNotificationCenter()
            center.getNotificationSettingsWithCompletionHandler { settings ->
                scope.launch(Dispatchers.Main) {
                    when (settings?.authorizationStatus) {
                        UNAuthorizationStatusAuthorized,
                        UNAuthorizationStatusProvisional,
                        -> { onPermissionGranted() }

                        UNAuthorizationStatusNotDetermined -> {
                            val options = UNAuthorizationOptionAlert or
                                UNAuthorizationOptionSound or
                                UNAuthorizationOptionBadge
                            center.requestAuthorizationWithOptions(options = options) { granted, _ ->
                                scope.launch(Dispatchers.Main) {
                                    if (granted) onPermissionGranted()
                                }
                            }
                        }

                        UNAuthorizationStatusDenied -> {
                            val settingsUrl = NSURL(string = UIApplicationOpenSettingsURLString)
                            if (UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
                                UIApplication.sharedApplication.openURL(settingsUrl)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}
