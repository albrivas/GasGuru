package com.gasguru.auto.common

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.gasguru.auto.ui.MapAutomotiveScreen

class GasGuruAutomotiveService : CarAppService() {
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return GasGuruSession()
    }
}

class GasGuruSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return MapAutomotiveScreen(carContext)
    }
}
