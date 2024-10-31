package com.gasguru.core.common

object CommonUtils {

    fun getAppVersion(): String {
        val versionName =
            "${BuildConfig.versionMajor}.${BuildConfig.versionMajor}.${BuildConfig.versionMajor}"
        return "$versionName (${BuildConfig.versionCode})"
    }
}
