package com.gasguru.core.domain

import com.gasguru.core.data.repository.UserDataRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke() = userDataRepository.userData
}
