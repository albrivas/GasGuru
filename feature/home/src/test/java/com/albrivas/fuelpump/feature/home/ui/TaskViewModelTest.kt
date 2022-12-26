package com.albrivas.fuelpump.feature.home.ui


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.albrivas.fuelpump.core.data.TaskRepository

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class TaskViewModelTest {
    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = TaskViewModel(FakeTaskRepository())
        assertEquals(viewModel.uiState.first(), TaskUiState.Loading)
    }

    @Test
    fun uiState_onItemSaved_isDisplayed() = runTest {
        val viewModel = TaskViewModel(FakeTaskRepository())
        assertEquals(viewModel.uiState.first(), TaskUiState.Loading)
    }
}

private class FakeTaskRepository : TaskRepository {

    private val data = mutableListOf<String>()

    override val tasks: Flow<List<String>>
        get() = flow { emit(data.toList()) }

    override suspend fun add(name: String) {
        data.add(0, name)
    }
}
