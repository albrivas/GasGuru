package com.gasguru

import android.app.Application
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GasGuruApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        oneSignalSetUp()
        mixpanelSetUp()
    }

    private fun oneSignalSetUp() {
        when (BuildConfig.DEBUG) {
            true -> OneSignal.Debug.logLevel = LogLevel.VERBOSE
            false -> OneSignal.Debug.logLevel = LogLevel.NONE
        }

        OneSignal.initWithContext(this, BuildConfig.onesignalAppId)
    }

    private fun mixpanelSetUp() {
        MixpanelAPI.getInstance(this, BuildConfig.mixpanelProjectToken, true)
    }
}
