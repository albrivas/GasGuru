package com.albrivas.fuelpump.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.albrivas.fuelpump.core.uikit.components.settings.SettingItem
import com.albrivas.fuelpump.core.uikit.components.settings.SettingItemModel
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.profile.R

@Composable
internal fun ProfileScreenRoute() {
    return ProfileScreen()
}

@Composable
internal fun ProfileScreen() {
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
    ) {
        SettingItem(
            model = SettingItemModel(
                title = stringResource(id = R.string.fuel_selection),
                selection = "Gasolina 95"
            )
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    MyApplicationTheme {
        ProfileScreen()
    }
}
