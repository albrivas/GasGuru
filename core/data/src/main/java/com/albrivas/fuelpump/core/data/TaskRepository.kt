package com.albrivas.fuelpump.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.albrivas.fuelpump.core.database.Task
import com.albrivas.fuelpump.core.database.TaskDao
import javax.inject.Inject

interface TaskRepository {
    val tasks: Flow<List<String>>

    suspend fun add(name: String)
}

class DefaultTaskRepository @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override val tasks: Flow<List<String>> =
        taskDao.getTasks().map { items -> items.map { it.name } }

    override suspend fun add(name: String) {
        taskDao.insertTask(Task(name = name))
    }
}
