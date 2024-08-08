package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.UserDataRepository
import javax.inject.Inject

class GetFavoriteStationsUseCase @Inject constructor(
    private val repository: UserDataRepository,
) {
    operator fun invoke() =
        repository.getUserWithFavoriteStations()
}
