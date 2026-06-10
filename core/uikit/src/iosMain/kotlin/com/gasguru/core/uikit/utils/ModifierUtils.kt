package com.gasguru.core.uikit.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

actual fun Modifier.maestroTestTag(tag: String): Modifier = this.testTag(tag)
