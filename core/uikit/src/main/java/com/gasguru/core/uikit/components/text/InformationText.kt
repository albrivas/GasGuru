package com.gasguru.core.uikit.components.text

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.TextSubtle

@Composable
fun InformationText(modifier: Modifier = Modifier, model: InformationTextModel) = with(model) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Image(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(id = icon),
                contentDescription = "fuel setting"
            )
        }

        Column(
            modifier = Modifier
                .weight(80f)
                .padding(start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Bottom)
        ) {
            Text(
                style = typography.bodySmall,
                text = title,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = TextSubtle,
                style = typography.displaySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationTextPreview() {
    MyApplicationTheme {
        InformationText(
            model = InformationTextModel(
                icon = R.drawable.ic_map,
                title = "Map",
                description = "L-V: 06:00-22:00; S: 07:00-22:00; D: 08:00-22:00".split(";")
                    .joinToString(separator = "\n") { it.trim() }
            )
        )
    }
}
