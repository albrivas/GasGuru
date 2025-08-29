package com.gasguru.core.uikit.components.searchlist

import com.gasguru.core.uikit.components.placeitem.PlaceItemModel

enum class SearchListType {
    RECENT,
    SUGGESTIONS
}

data class SearchListModel(
    val type: SearchListType,
    val items: List<PlaceItemModel>,
    val onClear: (() -> Unit)? = null
)
