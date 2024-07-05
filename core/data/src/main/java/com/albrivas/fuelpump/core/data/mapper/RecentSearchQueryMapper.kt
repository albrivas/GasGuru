package com.albrivas.fuelpump.core.data.mapper

import com.albrivas.fuelpump.core.database.model.RecentSearchQueryEntity
import com.albrivas.fuelpump.core.model.data.RecentSearchQuery

fun RecentSearchQueryEntity.asExternalModel() = RecentSearchQuery(
    name = name,
    id = id
)
