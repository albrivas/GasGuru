package com.gasguru.feature.detail_station.platform

import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

internal fun topMostViewController(): UIViewController? {
    val scene = UIApplication.sharedApplication.connectedScenes
        .mapNotNull { it as? UIWindowScene }
        .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
    val keyWindow = scene?.windows
        ?.mapNotNull { it as? UIWindow }
        ?.firstOrNull { it.isKeyWindow() }
        ?: UIApplication.sharedApplication.keyWindow
    var top: UIViewController = keyWindow?.rootViewController ?: return null
    while (true) {
        val presented = top.presentedViewController ?: return top
        top = presented
    }
}
