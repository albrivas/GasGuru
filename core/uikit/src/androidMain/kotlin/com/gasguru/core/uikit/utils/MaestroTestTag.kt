package com.gasguru.core.uikit.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.semantics.testTag as semanticsTestTag

actual fun Modifier.maestroTestTag(tag: String): Modifier = this
    .testTag(tag)
    .semantics {
        semanticsTestTag = tag
        testTagsAsResourceId = true
    }
