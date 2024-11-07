package com.gasguru.core.domain

import com.gasguru.core.data.repository.UserDataRepository
import com.gasguru.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<UserData> = userDataRepository.userData
}
