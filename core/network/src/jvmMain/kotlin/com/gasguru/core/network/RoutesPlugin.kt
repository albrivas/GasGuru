package com.gasguru.core.network

import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

// jvm target is test-only; no platform headers needed.
actual fun routesPlugin(packageName: String): HttpClientPlugin<*, *> =
    createClientPlugin(name = "RoutesPlugin") {}
