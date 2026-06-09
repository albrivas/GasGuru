package com.gasguru.core.ui.review

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.review.ReviewManagerFactory

@Composable
actual fun rememberInAppReviewManager(): InAppReviewManager? {
    val context = LocalContext.current
    val activity = context.findActivity()
    return remember(activity) {
        activity?.let {
            InAppReviewManagerAndroid(
                reviewManager = ReviewManagerFactory.create(context),
                activity = it,
            )
        }
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
