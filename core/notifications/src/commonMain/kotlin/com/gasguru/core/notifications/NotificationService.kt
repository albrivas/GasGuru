package com.gasguru.core.notifications

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("NotificationService", exact = true)
interface NotificationService {
    fun start()
}
