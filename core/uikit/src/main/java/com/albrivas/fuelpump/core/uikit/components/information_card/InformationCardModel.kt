package com.albrivas.fuelpump.core.uikit.components.information_card

data class InformationCardModel(
    val title: String,
    val description: String,
    val icon: Int,
    val onClick: () -> Unit,
)
