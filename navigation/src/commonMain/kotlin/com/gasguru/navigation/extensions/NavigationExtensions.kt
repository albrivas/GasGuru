package com.gasguru.navigation.extensions

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.savedstate.SavedState
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import com.gasguru.navigation.manager.NavigationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <reified T : Any> NavigationManager.navigateBackWith(key: String, value: T) {
    navigateBackWithData(key = key, value = encodeToSavedState(value))
}

fun NavController.setPreviousResult(key: String, value: SavedState?) {
    previousBackStackEntry?.savedStateHandle?.set(key, value)
}

inline fun <reified T : Any> NavBackStackEntry.getPreviousResult(key: String): T? {
    val savedState = this.savedStateHandle.get<SavedState>(key) ?: return null
    return decodeFromSavedState(savedState)
}

inline fun <reified T : Any> NavBackStackEntry.getResultStateFlow(key: String): Flow<T?> =
    savedStateHandle
        .getStateFlow<SavedState?>(key, null)
        .map { it?.let { decodeFromSavedState<T>(it) } }

fun NavBackStackEntry.removePreviousResult(key: String) {
    this.savedStateHandle.remove<SavedState>(key)
}