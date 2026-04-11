package com.gasguru.core.analytics

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.mixpanel.android.mpmetrics.MixpanelAPI
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("MixpanelAnalyticsHelper")
class MixpanelAnalyticsHelperTest {

    private val mixpanelApi: MixpanelAPI = mockk(relaxed = true)
    private val packageManager: PackageManager = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private lateinit var analyticsHelper: MixpanelAnalyticsHelper

    @BeforeEach
    fun setUp() {
        every { context.packageName } returns "com.gasguru"
        every { context.packageManager } returns packageManager
        every { packageManager.getPackageInfo("com.gasguru", 0) } returns PackageInfo().apply {
            versionName = "1.0.0"
        }
        analyticsHelper = MixpanelAnalyticsHelper(context = context, mixpanel = mixpanelApi)
    }

    @Test
    @DisplayName(
        """
        GIVEN analytics helper is created
        WHEN init runs
        THEN registers app_version and platform as super properties once
        """
    )
    fun initRegistersStaticSuperProperties() {
        val propertiesSlot = slot<JSONObject>()
        verify { mixpanelApi.registerSuperPropertiesOnce(capture(propertiesSlot)) }

        assertEquals("1.0.0", propertiesSlot.captured.getString("app_version"))
        assertEquals("android", propertiesSlot.captured.getString("platform"))
    }

    @Test
    @DisplayName(
        """
        GIVEN an analytics event with a type
        WHEN logEvent is called
        THEN mixpanel tracks the event with the correct type
        """
    )
    fun logEventTracksEventWithCorrectType() {
        val event = AnalyticsEvent(type = AnalyticsEvent.Types.VEHICLE_CREATED)

        analyticsHelper.logEvent(event = event)

        verify { mixpanelApi.track(AnalyticsEvent.Types.VEHICLE_CREATED, any()) }
    }

    @Test
    @DisplayName(
        """
        GIVEN an analytics event
        WHEN logEvent is called
        THEN the tracked properties include the category
        """
    )
    fun logEventAlwaysIncludesCategoryProperty() {
        val propertiesSlot = slot<JSONObject>()
        every { mixpanelApi.track(any(), capture(propertiesSlot)) } just runs
        val event = AnalyticsEvent(type = AnalyticsEvent.Types.VEHICLE_CREATED)

        analyticsHelper.logEvent(event = event)

        assertEquals(
            AnalyticsEvent.Categories.VEHICLE,
            propertiesSlot.captured.getString(AnalyticsEvent.ParamKeys.CATEGORY),
        )
    }

    @Test
    @DisplayName(
        """
        GIVEN an analytics event with extra params
        WHEN logEvent is called
        THEN the tracked properties include all extras as JSON properties
        """
    )
    fun logEventTracksExtrasAsJsonProperties() {
        val propertiesSlot = slot<JSONObject>()
        every { mixpanelApi.track(any(), capture(propertiesSlot)) } just runs
        val event = AnalyticsEvent(
            type = AnalyticsEvent.Types.VEHICLE_CREATED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.VEHICLE_TYPE, value = "CAR"),
                AnalyticsEvent.Param(
                    key = AnalyticsEvent.ParamKeys.FUEL_TYPE,
                    value = "GASOLINE_95"
                ),
            ),
        )

        analyticsHelper.logEvent(event = event)

        val capturedProperties = propertiesSlot.captured
        assertEquals("CAR", capturedProperties.getString(AnalyticsEvent.ParamKeys.VEHICLE_TYPE))
        assertEquals(
            "GASOLINE_95",
            capturedProperties.getString(AnalyticsEvent.ParamKeys.FUEL_TYPE)
        )
    }

    @Test
    @DisplayName(
        """
        GIVEN an analytics event with no extras
        WHEN logEvent is called
        THEN the tracked properties only contain the category
        """
    )
    fun logEventWithNoExtrasTracksOnlyCategory() {
        val propertiesSlot = slot<JSONObject>()
        every { mixpanelApi.track(any(), capture(propertiesSlot)) } just runs
        val event = AnalyticsEvent(type = AnalyticsEvent.Types.APP_OPENED)

        analyticsHelper.logEvent(event = event)

        assertEquals(
            AnalyticsEvent.Categories.SESSION,
            propertiesSlot.captured.getString(AnalyticsEvent.ParamKeys.CATEGORY),
        )
    }

    @Test
    @DisplayName(
        """
        GIVEN a map of dynamic properties
        WHEN updateSuperProperties is called
        THEN registers them via mixpanel.registerSuperProperties
        """
    )
    fun updateSuperPropertiesRegistersPropertiesAsSuperProperties() {
        val propertiesSlot = slot<JSONObject>()
        every { mixpanelApi.registerSuperProperties(capture(propertiesSlot)) } just runs

        analyticsHelper.updateSuperProperties(
            properties = mapOf(
                "primary_fuel_type" to "GASOLINE_95",
                "vehicle_count" to 2,
            ),
        )

        assertEquals("GASOLINE_95", propertiesSlot.captured.getString("primary_fuel_type"))
        assertEquals(2, propertiesSlot.captured.getInt("vehicle_count"))
    }
}
