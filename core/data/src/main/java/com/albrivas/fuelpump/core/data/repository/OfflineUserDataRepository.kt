package com.albrivas.fuelpump.core.data.repository

import com.albrivas.fuelpump.core.data.mapper.asEntity
import com.albrivas.fuelpump.core.database.dao.UserDataDao
import com.albrivas.fuelpump.core.database.model.asExternalModel
import com.albrivas.fuelpump.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineUserDataRepository @Inject constructor(
    private val userDataDao: UserDataDao
) : UserDataRepository {
    override val userData: Flow<UserData>
        get() = userDataDao.getUserData().map { it.asExternalModel() }

    override suspend fun updateUserData(userData: UserData) =
        userDataDao.insertUserData(userData.asEntity())
}
