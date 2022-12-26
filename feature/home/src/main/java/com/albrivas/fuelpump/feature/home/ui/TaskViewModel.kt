package com.albrivas.fuelpump.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.albrivas.fuelpump.core.data.TaskRepository
import com.albrivas.fuelpump.feature.home.ui.TaskUiState.Error
import com.albrivas.fuelpump.feature.home.ui.TaskUiState.Loading
import com.albrivas.fuelpump.feature.home.ui.TaskUiState.Success
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<TaskUiState> = taskRepository
        .tasks.map { Success(data = it) }
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addTask(name: String) {
        viewModelScope.launch {
            taskRepository.add(name)
        }
    }
}

sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Error(val throwable: Throwable) : TaskUiState
    data class Success(val data: List<String>) : TaskUiState
}
