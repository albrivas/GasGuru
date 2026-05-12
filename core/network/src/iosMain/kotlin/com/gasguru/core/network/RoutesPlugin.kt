package com.gasguru.core.network

import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header

actual fun routesPlugin(packageName: String): HttpClientPlugin<*, *> =
    createClientPlugin("RoutesPlugin") {
        onRequest { request, _ ->
            request.header("X-Ios-Bundle-Identifier", packageName)
            // TODO: add API key from xcconfig and iOS-specific restriction headers
        }
    }
