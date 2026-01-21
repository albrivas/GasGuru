package com.gasguru.core.uikit.components.information_card

import androidx.compose.ui.graphics.Color

data class InformationCardModel(
    val title: String,
    val subtitle: String,
    val description: String? = null,
    val icon: Int? = null,
    val subtitleColor: Color,
    val type: InformationCardType = InformationCardType.NONE,
    val onClick: () -> Unit = {},
) {
    enum class InformationCardType {
        NONE, EXPANDABLE
    }
}
