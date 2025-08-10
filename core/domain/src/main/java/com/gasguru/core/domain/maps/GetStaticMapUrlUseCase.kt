package com.gasguru.core.domain.maps

import android.location.Location
import com.gasguru.core.data.repository.maps.StaticMapRepository
import javax.inject.Inject

class GetStaticMapUrlUseCase @Inject constructor(
    private val staticMapRepository: StaticMapRepository,
) {
    operator fun invoke(
        location: Location,
        zoom: Int = 17,
        width: Int = 400,
        height: Int = 240,
    ): String = staticMapRepository.generateStaticMapUrl(
        location = location,
        zoom = zoom,
        width = width,
        height = height
    )
}