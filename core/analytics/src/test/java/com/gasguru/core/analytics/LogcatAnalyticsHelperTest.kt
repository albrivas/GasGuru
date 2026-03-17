package com.gasguru.core.analytics

import android.util.Log
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LogcatAnalyticsHelperTest {

    private lateinit var analyticsHelper: LogcatAnalyticsHelper

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        analyticsHelper = LogcatAnalyticsHelper()
    }

    @Test
    fun `logEvent logs event type to logcat`() {
        val event = AnalyticsEvent(type = AnalyticsEvent.Types.VEHICLE_CREATED)

        analyticsHelper.logEvent(event = event)

        val messageSlot = slot<String>()
        verify { Log.d("Analytics", capture(messageSlot)) }
        assertTrue(messageSlot.captured.contains(AnalyticsEvent.Types.VEHICLE_CREATED))
    }

    @Test
    fun `logEvent logs extras as key=value pairs`() {
        val event = AnalyticsEvent(
            type = AnalyticsEvent.Types.VEHICLE_CREATED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.VEHICLE_TYPE, value = "CAR"),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.FUEL_TYPE, value = "GASOLINE_95"),
            ),
        )

        analyticsHelper.logEvent(event = event)

        val messageSlot = slot<String>()
        verify { Log.d("Analytics", capture(messageSlot)) }
        val loggedMessage = messageSlot.captured
        assertTrue(loggedMessage.contains("${AnalyticsEvent.ParamKeys.VEHICLE_TYPE}=CAR"))
        assertTrue(loggedMessage.contains("${AnalyticsEvent.ParamKeys.FUEL_TYPE}=GASOLINE_95"))
    }

    @Test
    fun `logEvent logs dash when event has no extras`() {
        val event = AnalyticsEvent(type = AnalyticsEvent.Types.WENT_OFFLINE)

        analyticsHelper.logEvent(event = event)

        val messageSlot = slot<String>()
        verify { Log.d("Analytics", capture(messageSlot)) }
        assertTrue(messageSlot.captured.contains("—"))
    }
}
