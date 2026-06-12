package com.gasguru.core.ui.review

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIWindowScene

class InAppReviewManagerIos : InAppReviewManager {
    override suspend fun launchReviewFlow(
        onReviewCompleted: () -> Unit,
        onReviewFailed: (Exception) -> Unit,
    ) {
        withContext(Dispatchers.Main) {
            val scene = activeWindowScene()
            if (scene != null) {
                SKStoreReviewController.requestReviewInScene(scene)
                onReviewCompleted()
            }
        }
    }

    private fun activeWindowScene(): UIWindowScene? =
        UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
}
