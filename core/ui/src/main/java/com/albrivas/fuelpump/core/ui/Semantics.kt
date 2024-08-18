package com.albrivas.fuelpump.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

val IconTintKey = SemanticsPropertyKey<Color>("IconTint")
var SemanticsPropertyReceiver.iconTint by IconTintKey