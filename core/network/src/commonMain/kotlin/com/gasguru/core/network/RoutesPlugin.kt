package com.gasguru.core.network

import io.ktor.client.plugins.HttpClientPlugin

expect fun routesPlugin(packageName: String): HttpClientPlugin<*, *>
