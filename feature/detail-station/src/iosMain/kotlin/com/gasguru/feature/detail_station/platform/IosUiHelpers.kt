package com.gasguru.feature.detail_station.platform

import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowLevelAlert
import platform.UIKit.UIWindowScene
import platform.UIKit.popoverPresentationController

// Overlay window used to present UIAlertController / UIActivityViewController from a plain
// UIViewController, bypassing the UIHostingController that SwiftUI installs as the root VC.
// Presenting actionSheet from UIHostingController causes iOS to degrade it to .alert style.
private var overlayWindow: UIWindow? = null

internal fun presentInOverlayWindow(viewController: UIViewController) {
    val scene = UIApplication.sharedApplication.connectedScenes
        .mapNotNull { it as? UIWindowScene }
        .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
        ?: return
    val window = UIWindow(windowScene = scene)
    val hostVC = UIViewController()
    window.rootViewController = hostVC
    window.windowLevel = UIWindowLevelAlert + 1.0
    window.makeKeyAndVisible()
    overlayWindow = window
    // iOS 26+: actionSheet requires sourceView on popoverPresentationController;
    // without it the system falls back to a centered alert-style dialog.
    viewController.popoverPresentationController?.sourceView = hostVC.view
    hostVC.presentViewController(
        viewControllerToPresent = viewController,
        animated = true,
        completion = null,
    )
}

internal fun dismissOverlayWindow() {
    overlayWindow?.setHidden(true)
    overlayWindow = null
}

// Kept for contexts that still need a reference VC (e.g. future usage).
internal fun topMostViewController(): UIViewController? {
    val rootVC = UIApplication.sharedApplication.connectedScenes
        .mapNotNull { it as? UIWindowScene }
        .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
        ?.keyWindow
        ?.rootViewController
        ?: return null
    var top: UIViewController = rootVC
    var presented = top.presentedViewController
    while (presented != null) {
        top = presented
        presented = top.presentedViewController
    }
    return top
}
