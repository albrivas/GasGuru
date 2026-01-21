package com.gasguru.core.ui.review

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class DefaultInAppReviewManager(
    private val reviewManager: ReviewManager
) : InAppReviewManager {

    override suspend fun launchReviewFlow(
        activity: Activity,
        onReviewCompleted: () -> Unit,
        onReviewFailed: (Exception) -> Unit
    ) {
        try {
            val request = reviewManager.requestReviewFlow()

            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        onReviewCompleted()
                    }
                } else {
                    onReviewFailed(task.exception ?: Exception("Review request failed"))
                }
            }
        } catch (e: Exception) {
            onReviewFailed(e)
        }
    }
}

@Composable
fun rememberInAppReviewManager(): InAppReviewManager {
    val context = LocalContext.current
    return remember(context) {
        DefaultInAppReviewManager(ReviewManagerFactory.create(context))
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
