package com.gasguru.core.ui.review

interface InAppReviewManager {
    suspend fun launchReviewFlow(
        onReviewCompleted: () -> Unit,
        onReviewFailed: (Exception) -> Unit,
    )
}
