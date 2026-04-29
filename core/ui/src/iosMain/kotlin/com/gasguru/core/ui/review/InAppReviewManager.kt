package com.gasguru.core.ui.review

actual class InAppReviewManager {
    actual suspend fun launchReviewFlow(
        onReviewCompleted: () -> Unit,
        onReviewFailed: (Exception) -> Unit,
    ) {
        // V1: no-op. V2: SKStoreReviewController.requestReview()
    }
}
