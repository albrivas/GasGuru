package com.gasguru.core.supabase.model

data class NetworkError(
    val code: Int = -1,
    val message: String = "",
    val exception: Exception = Exception(),
)
