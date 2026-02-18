package com.gasguru.core.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header

fun routesPlugin(packageName: String, isDebug: Boolean) = createClientPlugin("RoutesPlugin") {
    val cert = if (isDebug) BuildKonfig.sha1Debug else BuildKonfig.sha1PlayStore

    onRequest { request, _ ->
        request.header("Content-Type", "application/json")
        request.header("X-Goog-Api-Key", BuildKonfig.googleApiKey)
        request.header("X-Goog-FieldMask", "*")
        request.header("X-Android-Package", packageName)
        request.header("X-Android-Cert", cert)
    }
}