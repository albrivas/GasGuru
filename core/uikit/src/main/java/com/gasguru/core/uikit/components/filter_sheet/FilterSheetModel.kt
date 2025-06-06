package com.gasguru.core.uikit.components.filter_sheet

data class FilterSheetModel(
    val title: String,
    val buttonText: String,
    val isMultiOption: Boolean,
    val isMustSelection: Boolean,
    val options: List<String>,
    val optionsSelected: List<String>,
    val onSaveButton: (List<String>) -> Unit,
    val onDismiss: () -> Unit,
    val type: FilterSheetType = FilterSheetType.NORMAL,
    val iconMap: Map<String, Int>? = null
)

enum class FilterSheetType {
    NORMAL, ICON
}
