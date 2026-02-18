package com.gasguru.core.network

import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header

actual fun routesPlugin(packageName: String): HttpClientPlugin<*, *> =
    createClientPlugin("RoutesPlugin") {
        onRequest { request, _ ->
            // TODO: add API key from xcconfig and iOS-specific restriction headers
            request.header("X-Ios-Bundle-Identifier", packageName)
        }
    }
