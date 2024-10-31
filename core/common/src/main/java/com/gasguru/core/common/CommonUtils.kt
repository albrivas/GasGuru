package com.gasguru.core.common

object CommonUtils {

    fun getAppVersion(): String {
        val versionName =
            "${BuildConfig.versionMajor}.${BuildConfig.versionMinor}.${BuildConfig.versionPatch}"
        return "$versionName (${BuildConfig.versionCode})"
    }
}
