package com.gasguru.core.testing.fakes.data.database

import com.gasguru.core.database.dao.UserDataDao
import com.gasguru.core.database.model.UserDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserDataDao(
    initialUserData: UserDataEntity? = null,
) : UserDataDao {

    private val userDataFlow = MutableStateFlow(initialUserData)

    override fun getUserData(): Flow<UserDataEntity?> = userDataFlow

    override suspend fun insertUserData(userData: UserDataEntity) {
        userDataFlow.value = userData
    }
}
