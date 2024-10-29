package com.gasguru.core.domain

import com.gasguru.core.data.repository.UserDataRepository
import com.gasguru.core.model.data.UserData
import javax.inject.Inject

class SaveUserDataUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(userData: UserData) =
        userDataRepository.updateUserData(userData)
}
