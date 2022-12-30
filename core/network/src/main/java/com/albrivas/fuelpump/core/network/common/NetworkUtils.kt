/*
 * File: NetworkUtils.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.main
 * Last modified: 12/28/22, 8:11 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network.common

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.albrivas.fuelpump.core.network.model.NetworkError

suspend fun <T> tryCall(action: suspend () -> T): Either<NetworkError, T> = try {
    action().right()
} catch (e: Exception) {
    NetworkError(exception = e).left()
}