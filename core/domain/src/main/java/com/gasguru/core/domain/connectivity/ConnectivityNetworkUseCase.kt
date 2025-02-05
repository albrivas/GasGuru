package com.gasguru.core.domain.connectivity

import com.gasguru.core.data.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectivityNetworkUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    operator fun invoke(): Flow<Boolean> = networkMonitor.isOnline
}
