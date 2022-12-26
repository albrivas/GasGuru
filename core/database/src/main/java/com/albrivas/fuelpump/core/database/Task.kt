package com.albrivas.fuelpump.core.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class Task(
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM task ORDER BY uid DESC LIMIT 10")
    fun getTasks(): Flow<List<Task>>

    @Insert
    suspend fun insertTask(item: Task)
}
