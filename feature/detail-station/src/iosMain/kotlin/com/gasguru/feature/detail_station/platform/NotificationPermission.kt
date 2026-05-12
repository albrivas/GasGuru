package com.gasguru.feature.detail_station.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberNotificationPermissionRequester(
    onPermissionGranted: () -> Unit,
): () -> Unit = { onPermissionGranted() }
