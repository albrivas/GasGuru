package com.gasguru.core.domain.fakes

import com.gasguru.core.data.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor(initialOnline: Boolean = true) : NetworkMonitor {
    private val onlineFlow = MutableStateFlow(initialOnline)

    override val isOnline: Flow<Boolean> = onlineFlow

    fun setOnline(online: Boolean) {
        onlineFlow.value = online
    }
}
