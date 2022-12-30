/*
 * File: NetworkError.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.main
 * Last modified: 12/30/22, 1:23 PM
 *
 * Created by albertorivas on 12/30/22, 1:25 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network.model

data class NetworkError(
    val code: Int = -1,
    val message: String = "",
    val exception: Exception = Exception()
)
