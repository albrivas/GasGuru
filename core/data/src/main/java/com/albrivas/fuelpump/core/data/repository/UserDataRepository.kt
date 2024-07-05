package com.albrivas.fuelpump.core.data.repository

import com.albrivas.fuelpump.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun updateUserData(userData: UserData)
}
