package com.gasguru.core.domain.user

import com.gasguru.core.domain.fakes.FakeUserDataRepository
import com.gasguru.core.model.data.ThemeMode
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveThemeModeUseCaseTest {

    private lateinit var sut: SaveThemeModeUseCase
    private lateinit var fakeUserDataRepository: FakeUserDataRepository

    @BeforeTest
    fun setUp() {
        fakeUserDataRepository = FakeUserDataRepository()
        sut = SaveThemeModeUseCase(userDataRepository = fakeUserDataRepository)
    }

    @Test
    fun savesThemeModeToRepository() = runTest {
        sut(themeMode = ThemeMode.DARK)

        assertEquals(1, fakeUserDataRepository.updatedThemeModes.size)
        assertEquals(ThemeMode.DARK, fakeUserDataRepository.updatedThemeModes.first())
    }

    @Test
    fun savingMultipleThemeModesTracksAll() = runTest {
        sut(themeMode = ThemeMode.DARK)
        sut(themeMode = ThemeMode.LIGHT)

        assertEquals(2, fakeUserDataRepository.updatedThemeModes.size)
        assertEquals(ThemeMode.LIGHT, fakeUserDataRepository.updatedThemeModes.last())
    }
}
