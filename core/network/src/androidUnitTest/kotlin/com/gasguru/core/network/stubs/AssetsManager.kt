package com.gasguru.core.network.stubs

import okio.buffer
import okio.source
import java.nio.charset.StandardCharsets

object AssetsManager {

    private const val basePath = "com/gasguru/core/network/responses/"

    fun getResponseJson(fileName: String): String {
        val fullPath = basePath + fileName
        val inputStream = javaClass.classLoader?.getResourceAsStream(fullPath)

        val source = inputStream?.let { inputStream.source().buffer() }
        return source?.readString(StandardCharsets.UTF_8) ?: "{}"
    }
}
