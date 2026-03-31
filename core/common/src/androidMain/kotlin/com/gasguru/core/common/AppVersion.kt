package com.gasguru.core.common

actual fun getAppVersion(): String {
    val versionName = "${BuildConfig.versionMajor}.${BuildConfig.versionMinor}.${BuildConfig.versionPatch}"
    return "$versionName (${BuildConfig.versionCode})"
}
