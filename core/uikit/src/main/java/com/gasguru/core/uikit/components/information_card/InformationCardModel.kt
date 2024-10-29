package com.gasguru.core.uikit.components.information_card

data class InformationCardModel(
    val title: String,
    val subtitle: String,
    val description: String? = null,
    val icon: Int? = null,
    val type: InformationCardType = InformationCardType.NONE,
    val onClick: () -> Unit = {},
) {
    enum class InformationCardType {
        NONE, EXPANDABLE
    }
}
