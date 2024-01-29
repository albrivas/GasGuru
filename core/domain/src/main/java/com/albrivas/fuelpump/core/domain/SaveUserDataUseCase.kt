package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.UserDataRepository
import com.albrivas.fuelpump.core.model.data.UserData
import javax.inject.Inject

class SaveUserDataUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(userData: UserData) =
        userDataRepository.updateUserData(userData)
}