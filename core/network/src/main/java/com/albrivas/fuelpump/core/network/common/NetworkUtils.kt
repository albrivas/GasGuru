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
