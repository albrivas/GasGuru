package com.gasguru.feature.detail_station.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberNotificationPermissionRequester(onPermissionGranted: () -> Unit): () -> Unit =
    remember { {} }
