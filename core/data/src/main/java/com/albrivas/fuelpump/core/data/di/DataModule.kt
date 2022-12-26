package com.albrivas.fuelpump.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.albrivas.fuelpump.core.data.TaskRepository
import com.albrivas.fuelpump.core.data.DefaultTaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsTaskRepository(
        taskRepository: DefaultTaskRepository
    ): TaskRepository
}

class FakeTaskRepository @Inject constructor() : TaskRepository {
    override val tasks: Flow<List<String>> = flowOf(fakeTasks)

    override suspend fun add(name: String) {
        throw NotImplementedError()
    }
}

val fakeTasks = listOf("One", "Two", "Three")
