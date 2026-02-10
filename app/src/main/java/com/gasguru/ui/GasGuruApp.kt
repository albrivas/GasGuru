package com.gasguru.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.R
import com.gasguru.core.uikit.components.alert.GasGuruAlertDialog
import com.gasguru.core.uikit.components.alert.GasGuruAlertDialogModel
import com.gasguru.core.uikit.components.alert_bar.AlertBar
import com.gasguru.core.uikit.components.alert_bar.AlertBarModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.navigation.root.GasGuruNavHost

@Composable
fun GasGuruApp(
    appState: GasGuruAppState,
    startDestination: Any = OnboardingRoutes.NewOnboardingRoute,
) {
    val context = LocalContext.current
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val isLocationDisabled by appState.isLocationDisabled.collectAsStateWithLifecycle()
    val isOnboardingComplete by appState.isOnboardingComplete.collectAsStateWithLifecycle()
    var showOfflineAlert by remember { mutableStateOf(false) }

    LaunchedEffect(isOffline) {
        showOfflineAlert = isOffline
    }

    Box(Modifier.fillMaxSize()) {
        GasGuruNavHost(startDestination = startDestination,)

        if (isLocationDisabled && isOnboardingComplete) {
            GasGuruAlertDialog(
                model = GasGuruAlertDialogModel(
                    icon = Icons.Outlined.LocationOff,
                    iconTint = GasGuruTheme.colors.accentOrange,
                    iconBackgroundColor = GasGuruTheme.colors.accentOrange.copy(alpha = 0.2f),
                    title = stringResource(id = com.gasguru.core.ui.R.string.alert_location_disabled_title),
                    description = stringResource(id = com.gasguru.core.ui.R.string.alert_location_disabled_description),
                    primaryButtonText = stringResource(id = com.gasguru.core.ui.R.string.alert_location_disabled_primary_button),
                ),
                onPrimaryButtonClick = {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                },
            )
        }

        AnimatedVisibility(
            visible = showOfflineAlert,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(
                    WindowInsets.statusBars.only(WindowInsetsSides.Top)
                )
        ) {
            AlertBar(
                modifier = Modifier.padding(16.dp),
                model = AlertBarModel(
                    message = stringResource(id = R.string.not_connected),
                    onDismiss = { showOfflineAlert = false }
                )
            )
        }
    }
}
