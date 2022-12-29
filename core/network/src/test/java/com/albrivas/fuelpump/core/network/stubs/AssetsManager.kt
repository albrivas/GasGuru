/*
 * File: AssetsManager.kt
 * Project: FuelPump
 * Module: FuelPump.core.network.unitTest
 * Last modified: 12/29/22, 5:02 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.network.stubs

import okio.buffer
import okio.source
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

object AssetsManager {

//    private const val basePath = "/src/test/assets/"
//
//    fun getResponseJson(nameFile: String): String {
//        return try {
//            readFile(nameFile)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            "{}"
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun readFile(path: String): String {
//        var reader: BufferedReader? = null
//        return try {
//            val userDirPath = System.getProperty("user.dir")?.let {
//                formatPath(
//                    it
//                )
//            }
//            val fullPath = userDirPath + basePath + path
//            reader = BufferedReader(InputStreamReader(FileInputStream(fullPath), "UTF-8"))
//            reader.use {
//                it.readText()
//            }
//        } catch (ignore: IOException) {
//            ""
//        } finally {
//            reader?.close()
//        }
//    }

    fun getResponseJson(fileName: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream(fileName)

        val source = inputStream?.let { inputStream.source().buffer() }
        return source?.readString(StandardCharsets.UTF_8) ?: "{}"
    }

//    private fun formatPath(path: String): String {
//        val paths = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        val listPaths = ArrayList(listOf(*paths))
//        return if (listPaths.contains("app")) {
//            path
//        } else {
//            "$path/app"
//        }
//    }
}