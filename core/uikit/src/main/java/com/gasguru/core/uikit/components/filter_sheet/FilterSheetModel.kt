package com.gasguru.core.uikit.components.filter_sheet

data class FilterSheetModel(
    val title: String,
    val buttonText: String,
    val isMultiOption: Boolean,
    val options: List<String>,
    val optionsSelected: List<String>,
    val onSaveButton: (List<String>) -> Unit,
    val onDismiss: () -> Unit,
)
