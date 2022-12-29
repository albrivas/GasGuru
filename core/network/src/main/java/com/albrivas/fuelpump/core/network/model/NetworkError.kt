/*
 * File: NetworkError.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.main
 * Last modified: 12/28/22, 7:41 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network.model

data class NetworkError(
    val code: Int = -1,
    val message: String = ""
)
