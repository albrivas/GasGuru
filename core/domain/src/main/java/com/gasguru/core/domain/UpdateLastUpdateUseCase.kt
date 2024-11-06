package com.gasguru.core.domain

import com.gasguru.core.data.repository.UserDataRepository
import javax.inject.Inject

class UpdateLastUpdateUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
) {
    suspend operator fun invoke() = userDataRepository.updateLastUpdate()
}
