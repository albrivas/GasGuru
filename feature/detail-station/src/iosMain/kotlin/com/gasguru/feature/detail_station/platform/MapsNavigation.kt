package com.gasguru.feature.detail_station.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import com.gasguru.core.model.data.LatLng
import platform.Foundation.NSURL
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIApplication

@Composable
actual fun rememberNavigateToMapsAction(stationName: String): (LatLng) -> Unit {
    val uriHandler = LocalUriHandler.current
    return remember(stationName) {
        {
                location ->
            val lat = location.latitude
            val lng = location.longitude
            val app = UIApplication.sharedApplication

            val sheet = UIAlertController.alertControllerWithTitle(
                title = stationName,
                message = "¿Con qué app quieres ir?",
                preferredStyle = UIAlertControllerStyleActionSheet,
            )

            sheet.addAction(
                UIAlertAction.actionWithTitle(
                    title = "Apple Maps",
                    style = UIAlertActionStyleDefault,
                ) { _ ->
                    dismissOverlayWindow()
                    uriHandler.openUri("http://maps.apple.com/?daddr=$lat,$lng")
                },
            )

            val gmapsUrl = NSURL(string = "comgooglemaps://?daddr=$lat,$lng&directionsmode=driving")
            if (app.canOpenURL(gmapsUrl)) {
                sheet.addAction(
                    UIAlertAction.actionWithTitle(
                        title = "Google Maps",
                        style = UIAlertActionStyleDefault,
                    ) { _ ->
                        dismissOverlayWindow()
                        uriHandler.openUri("comgooglemaps://?daddr=$lat,$lng&directionsmode=driving")
                    },
                )
            }

            val wazeUrl = NSURL(string = "waze://?ll=$lat,$lng&navigate=yes")
            if (app.canOpenURL(wazeUrl)) {
                sheet.addAction(
                    UIAlertAction.actionWithTitle(
                        title = "Waze",
                        style = UIAlertActionStyleDefault,
                    ) { _ ->
                        dismissOverlayWindow()
                        uriHandler.openUri("waze://?ll=$lat,$lng&navigate=yes")
                    },
                )
            }

            sheet.addAction(
                UIAlertAction.actionWithTitle(
                    title = "Cancel",
                    style = UIAlertActionStyleCancel,
                ) { _ -> dismissOverlayWindow() },
            )

            presentInOverlayWindow(sheet)
        }
    }
}
