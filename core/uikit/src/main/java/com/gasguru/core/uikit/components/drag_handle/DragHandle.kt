package com.gasguru.core.uikit.components.drag_handle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme

@Composable
fun DragHandle(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(vertical = 8.dp),
        color = GasGuruTheme.colors.neutral700,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(
            modifier = Modifier.size(
                width = 32.dp,
                height = 4.0.dp
            )
        )
    }
}
