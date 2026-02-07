package com.gasguru.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

val IconTintKey = SemanticsPropertyKey<Color>("IconTint")
var SemanticsPropertyReceiver.iconTint by IconTintKey

val BackgroundColorKey = SemanticsPropertyKey<Color>("BackgroundColor")
var SemanticsPropertyReceiver.backgroundColor by BackgroundColorKey
