package com.gasguru.core.uikit.theme

import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light Mode", showBackground = true, locale = "es")
@Preview(name = "Dark Mode", showBackground = true, locale = "en")
@Target(AnnotationTarget.FUNCTION)
actual annotation class ThemePreviews()
