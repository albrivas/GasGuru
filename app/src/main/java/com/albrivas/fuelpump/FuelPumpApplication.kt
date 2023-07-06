package com.albrivas.fuelpump

import android.app.Application
import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltAndroidApp
class FuelPumpApplication : Application()
