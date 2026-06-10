package com.gasguru.core.ui.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManager

class InAppReviewManagerAndroid(
    private val reviewManager: ReviewManager,
    private val activity: Activity,
) : InAppReviewManager {
    override suspend fun launchReviewFlow(
        onReviewCompleted: () -> Unit,
        onReviewFailed: (Exception) -> Unit,
    ) {
        try {
            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val flow = reviewManager.launchReviewFlow(activity, task.result)
                    flow.addOnCompleteListener { onReviewCompleted() }
                } else {
                    onReviewFailed(task.exception ?: Exception("Review request failed"))
                }
            }
        } catch (e: Exception) {
            onReviewFailed(e)
        }
    }
}