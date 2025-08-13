package com.gasguru.core.model.data

enum class ThemeMode(val id: Int) {
    DARK(1), LIGHT(2), SYSTEM(3);
    
    companion object {
        fun fromId(id: Int): ThemeMode = entries.find { it.id == id } ?: SYSTEM
    }
}