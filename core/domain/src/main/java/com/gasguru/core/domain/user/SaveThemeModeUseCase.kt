package com.gasguru.core.domain.user

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.model.data.ThemeMode

class SaveThemeModeUseCase(
    private val userDataRepository: UserDataRepository,
) {
    suspend operator fun invoke(themeMode: ThemeMode): Unit =
        userDataRepository.updateThemeMode(themeMode = themeMode)
}
