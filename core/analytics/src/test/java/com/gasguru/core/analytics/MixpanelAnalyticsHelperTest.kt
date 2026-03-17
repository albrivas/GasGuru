package com.gasguru.core.analytics

import com.mixpanel.android.mpmetrics.MixpanelAPI
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MixpanelAnalyticsHelperTest {

    private val mixpanelApi: MixpanelAPI = mockk(relaxed = true)
    private lateinit var analyticsHelper: MixpanelAnalyticsHelper

    @Before
    fun setUp() {
        analyticsHelper = MixpanelAnalyticsHelper(mixpanel = mixpanelApi)
    }

    @Test
    fun `logEvent tracks event with correct type`() {
        val event = AnalyticsEvent(type = AnalyticsEvent.Types.VEHICLE_CREATED)

        analyticsHelper.logEvent(event = event)

        val propertiesSlot = slot<JSONObject>()
        verify { mixpanelApi.track(AnalyticsEvent.Types.VEHICLE_CREATED, capture(propertiesSlot)) }
    }

    @Test
    fun `logEvent tracks event with extras as JSON properties`() {
        val event = AnalyticsEvent(
            type = AnalyticsEvent.Types.VEHICLE_CREATED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.VEHICLE_TYPE, value = "CAR"),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.FUEL_TYPE, value = "GASOLINE_95"),
            ),
        )

        analyticsHelper.logEvent(event = event)

        val propertiesSlot = slot<JSONObject>()
        verify { mixpanelApi.track(AnalyticsEvent.Types.VEHICLE_CREATED, capture(propertiesSlot)) }

        val capturedProperties = propertiesSlot.captured
        assertEquals("CAR", capturedProperties.getString(AnalyticsEvent.ParamKeys.VEHICLE_TYPE))
        assertEquals("GASOLINE_95", capturedProperties.getString(AnalyticsEvent.ParamKeys.FUEL_TYPE))
    }

    @Test
    fun `logEvent tracks event with empty extras as empty JSON`() {
        val event = AnalyticsEvent(type = AnalyticsEvent.Types.WENT_OFFLINE)

        analyticsHelper.logEvent(event = event)

        val propertiesSlot = slot<JSONObject>()
        verify { mixpanelApi.track(AnalyticsEvent.Types.WENT_OFFLINE, capture(propertiesSlot)) }

        assertEquals(0, propertiesSlot.captured.length())
    }
}
