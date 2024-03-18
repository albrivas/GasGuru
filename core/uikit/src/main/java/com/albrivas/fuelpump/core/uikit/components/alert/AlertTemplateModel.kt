package com.albrivas.fuelpump.core.uikit.components.alert

data class AlertTemplateModel(
    val icon: Int? = null,
    val animation: Int,
    val description: String,
    val buttonText: String,
    val onClick: () -> Unit
)
