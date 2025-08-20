package com.gasguru.navigation.extensions

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

/**
 * Generic extension function to set any value in the previous back stack entry's savedStateHandle
 */
inline fun <reified T> NavController.setPreviousResult(key: String, value: T?) {
    previousBackStackEntry?.savedStateHandle?.set(key, value)
}

inline fun <reified T> NavBackStackEntry.getPreviousResult(key: String): T? {
    return this.savedStateHandle[key]
}

fun NavBackStackEntry.removePreviousResult(key: String) {
    this.savedStateHandle.remove<String>(key)
}
