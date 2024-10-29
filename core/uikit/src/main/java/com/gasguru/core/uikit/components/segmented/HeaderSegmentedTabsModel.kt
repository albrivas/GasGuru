package com.gasguru.core.uikit.components.segmented

data class HeaderSegmentedTabsModel(
    val tabs: List<String>,
    val selectedTab: Int = 0,
    val onSelectedTab: (Int) -> Unit,
)
