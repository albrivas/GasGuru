package com.gasguru.core.data.sync

interface Syncable {
    suspend fun sync(): Boolean
    suspend fun hasPendingSync(): Boolean
}
