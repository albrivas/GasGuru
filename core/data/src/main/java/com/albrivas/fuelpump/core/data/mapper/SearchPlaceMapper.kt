package com.albrivas.fuelpump.core.data.mapper

import com.albrivas.fuelpump.core.model.data.SearchPlace
import com.google.android.libraries.places.api.model.AutocompletePrediction

fun AutocompletePrediction.toDomainModel() = SearchPlace(
    id = placeId,
    name = getFullText(null).toString(),
)

fun List<AutocompletePrediction>.toDomainModel() = map { it.toDomainModel() }
