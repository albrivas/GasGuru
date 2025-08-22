package com.gasguru.core.model.data

enum class FuelType(
    val extractPrice: (FuelStation) -> Double,
) {
    GASOLINE_95({ it.priceGasoline95E5 }),
    GASOLINE_95_PREMIUM({ it.priceGasoline95E5Premium }),
    GASOLINE_95_E10({ it.priceGasoline95E10 }),
    GASOLINE_98({ it.priceGasoline98E5 }),
    GASOLINE_98_PREMIUM({ it.priceGasoline98E10 }),
    DIESEL({ it.priceGasoilA }),
    DIESEL_PLUS({ it.priceGasoilPremium }),
    GASOIL_B({ it.priceGasoilB }),
}
