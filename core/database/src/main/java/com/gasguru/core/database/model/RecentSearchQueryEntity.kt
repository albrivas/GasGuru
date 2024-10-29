package com.gasguru.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent-search-queries"
)
data class RecentSearchQueryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
)
