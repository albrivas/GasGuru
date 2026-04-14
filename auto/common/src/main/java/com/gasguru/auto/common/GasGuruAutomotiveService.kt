package com.gasguru.auto.common

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.gasguru.auto.analytics.trackAutoSessionStarted
import com.gasguru.auto.ui.mainmenu.MapAutomotiveScreen
import com.gasguru.core.analytics.AnalyticsHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GasGuruAutomotiveService : CarAppService() {
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return GasGuruSession()
    }
}

class GasGuruSession : Session(), KoinComponent {

    private val analyticsHelper: AnalyticsHelper by inject()

    override fun onCreateScreen(intent: Intent): Screen {
        analyticsHelper.trackAutoSessionStarted()
        return MapAutomotiveScreen(carContext)
    }
}
