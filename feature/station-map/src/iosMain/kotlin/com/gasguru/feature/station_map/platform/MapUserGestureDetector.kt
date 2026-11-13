package com.gasguru.feature.station_map.platform

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.ObjCSignatureOverride
import platform.Foundation.NSSelectorFromString
import platform.MapKit.MKMapView
import platform.UIKit.UIGestureRecognizer
import platform.UIKit.UIGestureRecognizerDelegateProtocol
import platform.UIKit.UIGestureRecognizerStateBegan
import platform.UIKit.UIPanGestureRecognizer
import platform.UIKit.UIPinchGestureRecognizer
import platform.UIKit.UIRotationGestureRecognizer
import platform.darwin.NSObject

// MapKit's own recognizers live in private subviews that change between iOS versions,
// so we attach passive recognizers of our own that run alongside them.
@OptIn(ExperimentalForeignApi::class)
internal class MapUserGestureDetector : NSObject(), UIGestureRecognizerDelegateProtocol {

    private var hasPendingUserGesture = false

    fun attachTo(mapView: MKMapView) {
        val gestureAction = NSSelectorFromString("onUserGesture:")
        listOf(
            UIPanGestureRecognizer(target = this, action = gestureAction),
            UIPinchGestureRecognizer(target = this, action = gestureAction),
            UIRotationGestureRecognizer(target = this, action = gestureAction),
        ).forEach { recognizer ->
            recognizer.delegate = this
            recognizer.cancelsTouchesInView = false
            recognizer.delaysTouchesBegan = false
            recognizer.delaysTouchesEnded = false
            mapView.addGestureRecognizer(recognizer)
        }
    }

    fun consumePendingUserGesture(): Boolean {
        val wasUserGesture = hasPendingUserGesture
        hasPendingUserGesture = false
        return wasUserGesture
    }

    @OptIn(BetaInteropApi::class)
    @ObjCAction
    fun onUserGesture(recognizer: UIGestureRecognizer) {
        if (recognizer.state == UIGestureRecognizerStateBegan) {
            hasPendingUserGesture = true
        }
    }

    @ObjCSignatureOverride
    override fun gestureRecognizer(
        gestureRecognizer: UIGestureRecognizer,
        shouldRecognizeSimultaneouslyWithGestureRecognizer: UIGestureRecognizer,
    ): Boolean = true
}
