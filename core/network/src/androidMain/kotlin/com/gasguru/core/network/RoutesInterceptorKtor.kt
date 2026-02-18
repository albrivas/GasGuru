package com.gasguru.core.network

import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header

actual fun routesPlugin(packageName: String): HttpClientPlugin<*, *> =
    createClientPlugin("RoutesPlugin") {
        val cert = if (BuildConfig.DEBUG) BuildConfig.sha1Debug else BuildConfig.sha1PlayStore

        onRequest { request, _ ->
            request.header("Content-Type", "application/json")
            request.header("X-Goog-Api-Key", BuildConfig.googleApiKey)
            request.header("X-Goog-FieldMask", "*")
            request.header("X-Android-Package", packageName)
            request.header("X-Android-Cert", cert)
        }
    }
