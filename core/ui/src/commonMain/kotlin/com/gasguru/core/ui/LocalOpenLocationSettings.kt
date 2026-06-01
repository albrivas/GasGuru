package com.gasguru.core.ui

import androidx.compose.runtime.staticCompositionLocalOf

val LocalOpenLocationSettings = staticCompositionLocalOf<() -> Unit> {
    error("LocalOpenLocationSettings not provided")
}
