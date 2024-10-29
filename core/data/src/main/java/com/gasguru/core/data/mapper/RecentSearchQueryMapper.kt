package com.gasguru.core.data.mapper

import com.gasguru.core.database.model.RecentSearchQueryEntity
import com.gasguru.core.model.data.RecentSearchQuery

fun RecentSearchQueryEntity.asExternalModel() = RecentSearchQuery(
    name = name,
    id = id
)
