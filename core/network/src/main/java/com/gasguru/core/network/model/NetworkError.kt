package com.gasguru.core.network.model

data class NetworkError(
    val code: Int = -1,
    val message: String = "",
    val exception: Exception = Exception()
)
