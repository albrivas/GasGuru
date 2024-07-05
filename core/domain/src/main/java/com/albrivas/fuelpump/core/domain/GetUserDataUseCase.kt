package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.UserDataRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke() = userDataRepository.userData
}
