package com.gasguru.core.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// V1 stub: asume siempre online. Para iOS V2: implementar con NWPathMonitor (Network framework).
class NetworkMonitorIos : NetworkMonitor {
    override val isOnline: Flow<Boolean> = flowOf(true)
}
