package com.gasguru.core.ui.review

import androidx.compose.runtime.Composable

@Composable
actual fun rememberInAppReviewManager(): InAppReviewManager? = InAppReviewManagerIos()
