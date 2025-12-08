package com.gasguru.core.ui.review

import android.app.Activity

interface InAppReviewManager {
    suspend fun launchReviewFlow(
        activity: Activity,
        onReviewCompleted: () -> Unit = {},
        onReviewFailed: (Exception) -> Unit = {}
    )
}
