package com.gasguru.core.uikit.components.alert_bar

data class AlertBarModel(
    val message: String,
    val onDismiss: () -> Unit
)
