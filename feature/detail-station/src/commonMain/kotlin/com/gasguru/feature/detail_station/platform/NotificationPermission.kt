package com.gasguru.feature.detail_station.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionRequester(
    onPermissionGranted: () -> Unit,
): () -> Unit
