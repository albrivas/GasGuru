package com.gasguru.core.uikit.components.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun GasGuruAlertDialog(
    model: GasGuruAlertDialogModel,
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSecondaryButtonClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Transparent.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .background(
                    color = GasGuruTheme.colors.neutralWhite,
                    shape = RoundedCornerShape(size = 24.dp),
                )
                .padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 24.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(size = 72.dp)
                    .background(
                        color = model.iconBackgroundColor,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = model.icon,
                    contentDescription = null,
                    tint = model.iconTint,
                    modifier = Modifier.size(size = 32.dp),
                )
            }

            Text(
                text = model.title,
                style = GasGuruTheme.typography.h5,
                color = GasGuruTheme.colors.textMain,
                textAlign = TextAlign.Center,
            )

            Text(
                text = model.description,
                style = GasGuruTheme.typography.smallRegular.copy(lineHeight = 21.sp),
                color = GasGuruTheme.colors.textSubtle,
                textAlign = TextAlign.Center,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(space = 12.dp),
            ) {
                GasGuruButton(
                    onClick = onPrimaryButtonClick,
                    text = model.primaryButtonText,
                    shape = RoundedCornerShape(size = 16.dp),
                    height = 52.dp,
                )

                if (model.secondaryButtonText != null) {
                    GasGuruButton(
                        onClick = onSecondaryButtonClick,
                        text = model.secondaryButtonText,
                        containerColor = GasGuruTheme.colors.neutral200,
                        contentColor = GasGuruTheme.colors.textSubtle,
                    )
                }
            }
        }
    }
}

@Composable
@ThemePreviews
private fun GasGuruAlertDialogPreview() {
    MyApplicationTheme {
        GasGuruAlertDialog(
            model = GasGuruAlertDialogModel(
                icon = Icons.Outlined.LocationOff,
                iconTint = Color(0xFFF59E0B),
                iconBackgroundColor = Color(0xFFFFFBEB),
                title = "Ubicación desactivada",
                description = "Activa la ubicación en los ajustes de tu dispositivo para ver las gasolineras cercanas.",
                primaryButtonText = "Activar ubicación",
                secondaryButtonText = "Ahora no",
            ),
            onPrimaryButtonClick = {},
            onSecondaryButtonClick = {},
        )
    }
}
