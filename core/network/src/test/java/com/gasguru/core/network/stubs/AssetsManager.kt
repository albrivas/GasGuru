package com.gasguru.core.network.stubs

import okio.buffer
import okio.source
import java.nio.charset.StandardCharsets

object AssetsManager {

    fun getResponseJson(fileName: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream(fileName)

        val source = inputStream?.let { inputStream.source().buffer() }
        return source?.readString(StandardCharsets.UTF_8) ?: "{}"
    }
}
