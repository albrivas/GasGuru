/*
 * File: AssetsManager.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.unitTest
 * Last modified: 12/30/22, 1:24 PM
 *
 * Created by albertorivas on 12/30/22, 1:26 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

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
