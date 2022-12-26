package com.albrivas.fuelpump.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.albrivas.fuelpump.core.data.DefaultTaskRepository
import com.albrivas.fuelpump.core.database.Task
import com.albrivas.fuelpump.core.database.TaskDao

/**
 * Unit tests for [DefaultTaskRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultTaskRepositoryTest {

    @Test
    fun tasks_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultTaskRepository(FakeTaskDao())

        repository.add("Repository")

        assertEquals(repository.tasks.first().size, 1)
    }

}

private class FakeTaskDao : TaskDao {

    private val data = mutableListOf<Task>()

    override fun getTasks(): Flow<List<Task>> = flow {
        emit(data)
    }

    override suspend fun insertTask(item: Task) {
        data.add(0, item)
    }
}
