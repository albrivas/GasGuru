package com.gasguru.core.uikit.components.alert_bar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun AlertBar(model: AlertBarModel, modifier: Modifier = Modifier) = with(model) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = GasGuruTheme.colors.neutral300,
        shape = MaterialTheme.shapes.small,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = GasGuruTheme.typography.baseRegular,
                color = GasGuruTheme.colors.textMain,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = GasGuruTheme.colors.neutralBlack
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun AlertBarPreview() {
    AlertBar(model = AlertBarModel(message = "No tienes conexion a internet", onDismiss = {}))
}
