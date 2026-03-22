package com.gasguru.core.common

import platform.Foundation.NSBundle

actual fun getAppVersion(): String {
    val version = NSBundle.mainBundle.infoDictionary
        ?.get("CFBundleShortVersionString") as? String ?: "unknown"
    val build = NSBundle.mainBundle.infoDictionary
        ?.get("CFBundleVersion") as? String ?: "0"
    return "$version ($build)"
}
