package com.gasguru.core.domain.user

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.model.data.ThemeMode
import javax.inject.Inject

class SaveThemeModeUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
) {
    suspend operator fun invoke(themeMode: ThemeMode): Unit =
        userDataRepository.updateThemeMode(themeMode = themeMode)
}
