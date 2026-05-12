package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ThemeModeUiMapperTest {

    @Test
    fun givenDarkThemeMode_whenMappingToUi_thenReturnsDarkEntry() {
        val result = ThemeMode.DARK.toUi()
        assertEquals(ThemeMode.DARK, result.mode)
        assertNotNull(result.iconRes)
        assertNotNull(result.titleRes)
    }

    @Test
    fun givenLightThemeMode_whenMappingToUi_thenReturnsLightEntry() {
        val result = ThemeMode.LIGHT.toUi()
        assertEquals(ThemeMode.LIGHT, result.mode)
        assertNotNull(result.iconRes)
        assertNotNull(result.titleRes)
    }

    @Test
    fun givenSystemThemeMode_whenMappingToUi_thenReturnsSystemEntry() {
        val result = ThemeMode.SYSTEM.toUi()
        assertEquals(ThemeMode.SYSTEM, result.mode)
        assertNotNull(result.iconRes)
        assertNotNull(result.titleRes)
    }

    @Test
    fun givenAllThemeModes_whenMappingToUi_thenAllHaveDistinctTitleRes() {
        val titleRefs = ThemeMode.entries.map { it.toUi().titleRes }
        assertEquals(titleRefs.size, titleRefs.toSet().size, "Duplicate titleRes across ThemeModeUi entries")
    }
}
