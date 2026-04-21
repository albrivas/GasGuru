package com.gasguru.data.fakes

import com.gasguru.core.database.dao.UserDataDao
import com.gasguru.core.database.model.UserDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeUserDataDao(
    initialData: UserDataEntity? = null,
) : UserDataDao {

    private val dataFlow = MutableStateFlow(initialData)

    override fun getUserData(): Flow<UserDataEntity?> = dataFlow

    override suspend fun insertUserData(userData: UserDataEntity): Long =
        if (dataFlow.value != null) {
            -1L
        } else {
            dataFlow.value = userData
            userData.id
        }

    override suspend fun updateUserData(userData: UserDataEntity) {
        dataFlow.update { userData }
    }
}
