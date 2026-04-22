package com.gasguru.core.domain.user

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

class GetUserDataUseCase(
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<UserData> = userDataRepository.userData
}
