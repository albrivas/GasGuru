package com.albrivas.fuelpump.feature.onboarding_welcome.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.components.FuelPumpButton
import com.albrivas.fuelpump.core.uikit.theme.GrayLight
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.onboarding.R
import com.albrivas.fuelpump.core.uikit.R as RUikit

@Composable
fun OnboardingWelcomeScreenRoute(navigateToSelectFuel: () -> Unit) {
    var locationPermissionGranted by remember { mutableStateOf(false) }

    val requestMultiplePermissionsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                locationPermissionGranted =
                    permissions[ACCESS_FINE_LOCATION] == true || permissions[ACCESS_COARSE_LOCATION] == true
            }
        )

    LaunchedEffect(Unit) {
        requestMultiplePermissionsLauncher.launch(
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
        )
    }

    OnboardingWelcomeScreen(navigateToSelectFuel, locationPermissionGranted)
}

@Composable
internal fun OnboardingWelcomeScreen(
    navigateToSelectFuel: () -> Unit = {},
    isPermissionGranted: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = RUikit.drawable.ic_welcome),
            contentDescription = "Welcome image",
            modifier = Modifier.padding(top = 30.dp)
        )

        Spacer(modifier = Modifier.height(66.dp))
        Text(
            text = stringResource(id = R.string.welcome),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(id = R.string.welcome_text),
            color = GrayLight,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.welcome_permission),
            color = GrayLight,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.weight(1f))
        FuelPumpButton(
            onClick = navigateToSelectFuel,
            enabled = isPermissionGranted,
            text = stringResource(id = R.string.welcome_button),
            modifier = Modifier
                .padding(bottom = 17.dp)
                .systemBarsPadding()
                .testTag("button_next")
        )
    }
}

@Composable
@Preview
private fun OnboardingWelcomeScreenPreview() {
    MyApplicationTheme {
        OnboardingWelcomeScreen(isPermissionGranted = false)
    }
}
