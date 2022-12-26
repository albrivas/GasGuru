/*
 * nnnnnnn
 */

package com.albrivas.fuelpump.testdi

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import com.albrivas.fuelpump.core.data.TaskRepository
import com.albrivas.fuelpump.core.data.di.DataModule
import com.albrivas.fuelpump.core.data.di.FakeTaskRepository

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface FakeDataModule {

    @Binds
    abstract fun bindRepository(
        fakeRepository: FakeTaskRepository
    ): TaskRepository
}
