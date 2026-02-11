package com.gasguru.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.gasguru.core.data.util.NetworkMonitor
import com.gasguru.core.domain.location.IsLocationEnabledUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberGasGuruAppState(
    networkMonitor: NetworkMonitor,
    isLocationEnabledUseCase: IsLocationEnabledUseCase,
    getUserDataUseCase: GetUserDataUseCase,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember(networkMonitor, isLocationEnabledUseCase, getUserDataUseCase, coroutineScope) {
    GasGuruAppState(networkMonitor, isLocationEnabledUseCase, getUserDataUseCase, coroutineScope)
}

@Stable
class GasGuruAppState(
    networkMonitor: NetworkMonitor,
    isLocationEnabledUseCase: IsLocationEnabledUseCase,
    getUserDataUseCase: GetUserDataUseCase,
    coroutineScope: CoroutineScope,
) {

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isLocationDisabled = isLocationEnabledUseCase()
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isOnboardingComplete = getUserDataUseCase()
        .map { it.isOnboardingSuccess }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}
