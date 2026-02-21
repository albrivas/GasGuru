package com.gasguru.core.domain.maps

import com.gasguru.core.data.repository.maps.StaticMapRepository
import com.gasguru.core.model.data.LatLng

class GetStaticMapUrlUseCase(
    private val staticMapRepository: StaticMapRepository,
) {
    operator fun invoke(
        location: LatLng,
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
