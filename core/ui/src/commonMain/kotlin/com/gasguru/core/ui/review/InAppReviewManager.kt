package com.gasguru.core.ui.review

expect class InAppReviewManager {
    suspend fun launchReviewFlow(
        onReviewCompleted: () -> Unit,
        onReviewFailed: (Exception) -> Unit,
    )
}
