package com.gasguru.core.domain.connectivity

import com.gasguru.core.data.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow

class ConnectivityNetworkUseCase(
    private val networkMonitor: NetworkMonitor
) {
    operator fun invoke(): Flow<Boolean> = networkMonitor.isOnline
}
