package com.albrivas.fuelpump.core.uikit.components.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun InformationText(modifier: Modifier = Modifier, model: InformationTextModel) = with(model) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = description
        )
        Text(
            text = title,
            style = typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun InformationTextPreview() {
    MyApplicationTheme {
        InformationText(
            model = InformationTextModel(
                icon = R.drawable.ic_map,
                title = "Map",
                description = "Description"
            )
        )
    }
}