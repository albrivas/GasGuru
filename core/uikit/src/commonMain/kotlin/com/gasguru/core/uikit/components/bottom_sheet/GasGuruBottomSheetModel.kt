package com.gasguru.core.uikit.components.bottom_sheet

import androidx.compose.runtime.Immutable

@Immutable
data class GasGuruBottomSheetModel(
    val title: String,
    val onDismiss: () -> Unit,
    val subtitle: String? = null,
    val actionButton: GasGuruBottomSheetAction? = null,
    val skipPartiallyExpanded: Boolean = false,
)

@Immutable
data class GasGuruBottomSheetAction(
    val text: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)
