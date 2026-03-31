package com.gasguru.core.network

import android.content.Context
import okhttp3.Interceptor

class RoutesInterceptor(
    private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()

        val cert = if (BuildConfig.DEBUG) BuildConfig.sha1Debug else BuildConfig.sha1PlayStore

        val newRequest = request.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Goog-Api-Key", BuildConfig.googleApiKey)
            .addHeader("X-Goog-FieldMask", "*")
            .addHeader("X-Android-Package", context.packageName)
            .addHeader("X-Android-Cert", cert)
            .build()

        return chain.proceed(newRequest)
    }
}
