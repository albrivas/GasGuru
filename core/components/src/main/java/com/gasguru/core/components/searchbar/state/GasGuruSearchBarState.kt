package com.gasguru.core.components.searchbar.state

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberGasGuruSearchBarState(alwaysActive: Boolean): GasGuruSearchBarState {
    return remember { GasGuruSearchBarState(alwaysActive = alwaysActive) }
}

@Stable
class GasGuruSearchBarState(alwaysActive: Boolean) {
    var active by mutableStateOf(alwaysActive)
        private set

    val paddingAnimation: Dp
        @Composable get() = animateDpAsState(
            targetValue = if (active) 0.dp else 16.dp,
            animationSpec = tween(durationMillis = 300),
            label = "search_bar_padding"
        ).value

    val statusBarPaddingAnimation: Dp
        @Composable get() = animateDpAsState(
            targetValue = 0.dp,
            animationSpec = tween(durationMillis = 300),
            label = "search_bar_status_padding"
        ).value

    fun deactivate() {
        active = false
    }

    fun deactivateWithFocusClear(onClearFocus: () -> Unit) {
        active = false
        onClearFocus()
    }

    fun onFocusReceived() {
        if (!active) {
            active = true
        }
    }

    fun onExpandedChange(newActive: Boolean) {
        active = newActive
    }
}
