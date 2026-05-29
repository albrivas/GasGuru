package com.gasguru.feature.detail_station

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DateUtilsTest {

    // region classifyTimeDiff

    @Test
    fun `GIVEN time diff of 0 ms WHEN classifyTimeDiff is called THEN result is Now`() {
        val result = classifyTimeDiff(timeDiff = 0L)
        assertEquals(TimeElapsedCategory.Now, result)
    }

    @Test
    fun `GIVEN time diff of one minute minus 1 ms WHEN classifyTimeDiff is called THEN result is Now`() {
        val result = classifyTimeDiff(timeDiff = ONE_MINUTE_MS - 1L)
        assertEquals(TimeElapsedCategory.Now, result)
    }

    @Test
    fun `GIVEN time diff of exactly one minute WHEN classifyTimeDiff is called THEN result is OneMinute with minutes equal to 1`() {
        val result = classifyTimeDiff(timeDiff = ONE_MINUTE_MS)
        assertIs<TimeElapsedCategory.OneMinute>(result)
        assertEquals(1L, result.minutes)
    }

    @Test
    fun `GIVEN time diff of 5 minutes WHEN classifyTimeDiff is called THEN result is Minutes with minutes equal to 5`() {
        val result = classifyTimeDiff(timeDiff = 5 * ONE_MINUTE_MS)
        assertIs<TimeElapsedCategory.Minutes>(result)
        assertEquals(5L, result.minutes)
    }

    @Test
    fun `GIVEN time diff of one hour minus 1 ms WHEN classifyTimeDiff is called THEN result is Minutes with minutes equal to 59`() {
        val result = classifyTimeDiff(timeDiff = ONE_HOUR_MS - 1L)
        assertIs<TimeElapsedCategory.Minutes>(result)
        assertEquals(59L, result.minutes)
    }

    @Test
    fun `GIVEN time diff of exactly one hour WHEN classifyTimeDiff is called THEN result is OneHour with hours equal to 1`() {
        val result = classifyTimeDiff(timeDiff = ONE_HOUR_MS)
        assertIs<TimeElapsedCategory.OneHour>(result)
        assertEquals(1L, result.hours)
    }

    @Test
    fun `GIVEN time diff of 3 hours WHEN classifyTimeDiff is called THEN result is Hours with hours equal to 3`() {
        val result = classifyTimeDiff(timeDiff = 3 * ONE_HOUR_MS)
        assertIs<TimeElapsedCategory.Hours>(result)
        assertEquals(3L, result.hours)
    }

    @Test
    fun `GIVEN time diff of one day minus 1 ms WHEN classifyTimeDiff is called THEN result is Hours with hours equal to 23`() {
        val result = classifyTimeDiff(timeDiff = ONE_DAY_MS - 1L)
        assertIs<TimeElapsedCategory.Hours>(result)
        assertEquals(23L, result.hours)
    }

    @Test
    fun `GIVEN time diff of exactly one day WHEN classifyTimeDiff is called THEN result is OneDay with days equal to 1`() {
        val result = classifyTimeDiff(timeDiff = ONE_DAY_MS)
        assertIs<TimeElapsedCategory.OneDay>(result)
        assertEquals(1L, result.days)
    }

    @Test
    fun `GIVEN time diff of 3 days WHEN classifyTimeDiff is called THEN result is Days with days equal to 3`() {
        val result = classifyTimeDiff(timeDiff = 3 * ONE_DAY_MS)
        assertIs<TimeElapsedCategory.Days>(result)
        assertEquals(3L, result.days)
    }

    @Test
    fun `GIVEN time diff of one week minus 1 ms WHEN classifyTimeDiff is called THEN result is Days with days equal to 6`() {
        val result = classifyTimeDiff(timeDiff = ONE_WEEK_MS - 1L)
        assertIs<TimeElapsedCategory.Days>(result)
        assertEquals(6L, result.days)
    }

    @Test
    fun `GIVEN time diff of exactly one week WHEN classifyTimeDiff is called THEN result is LongAgo`() {
        val result = classifyTimeDiff(timeDiff = ONE_WEEK_MS)
        assertEquals(TimeElapsedCategory.LongAgo, result)
    }

    @Test
    fun `GIVEN time diff of 30 days WHEN classifyTimeDiff is called THEN result is LongAgo`() {
        val result = classifyTimeDiff(timeDiff = 30 * ONE_DAY_MS)
        assertEquals(TimeElapsedCategory.LongAgo, result)
    }

    // endregion

    // region resolveScheduleDayRange

    private val spanishDaysMap = mapOf(
        "L" to "Lun",
        "M" to "Mar",
        "X" to "Mié",
        "J" to "Jue",
        "V" to "Vie",
        "S" to "Sáb",
        "D" to "Dom",
    )

    @Test
    fun `GIVEN day range L-V WHEN resolveScheduleDayRange is called THEN result is Lun-Vie`() {
        val result = resolveScheduleDayRange(dayRange = "L-V", daysMap = spanishDaysMap)
        assertEquals("Lun-Vie", result)
    }

    @Test
    fun `GIVEN day range L-S WHEN resolveScheduleDayRange is called THEN result is Lun-Sab`() {
        val result = resolveScheduleDayRange(dayRange = "L-S", daysMap = spanishDaysMap)
        assertEquals("Lun-Sáb", result)
    }

    @Test
    fun `GIVEN day range L-D WHEN resolveScheduleDayRange is called THEN result is Lun-Dom`() {
        val result = resolveScheduleDayRange(dayRange = "L-D", daysMap = spanishDaysMap)
        assertEquals("Lun-Dom", result)
    }

    @Test
    fun `GIVEN day range M-V WHEN resolveScheduleDayRange is called THEN result is Mar-Vie`() {
        val result = resolveScheduleDayRange(dayRange = "M-V", daysMap = spanishDaysMap)
        assertEquals("Mar-Vie", result)
    }

    @Test
    fun `GIVEN day range M-S WHEN resolveScheduleDayRange is called THEN result is Mar-Sab`() {
        val result = resolveScheduleDayRange(dayRange = "M-S", daysMap = spanishDaysMap)
        assertEquals("Mar-Sáb", result)
    }

    @Test
    fun `GIVEN day range M-D WHEN resolveScheduleDayRange is called THEN result is Mar-Dom`() {
        val result = resolveScheduleDayRange(dayRange = "M-D", daysMap = spanishDaysMap)
        assertEquals("Mar-Dom", result)
    }

    @Test
    fun `GIVEN day range X-V WHEN resolveScheduleDayRange is called THEN result is Mie-Vie`() {
        val result = resolveScheduleDayRange(dayRange = "X-V", daysMap = spanishDaysMap)
        assertEquals("Mié-Vie", result)
    }

    @Test
    fun `GIVEN day range X-S WHEN resolveScheduleDayRange is called THEN result is Mie-Sab`() {
        val result = resolveScheduleDayRange(dayRange = "X-S", daysMap = spanishDaysMap)
        assertEquals("Mié-Sáb", result)
    }

    @Test
    fun `GIVEN day range X-D WHEN resolveScheduleDayRange is called THEN result is Mie-Dom`() {
        val result = resolveScheduleDayRange(dayRange = "X-D", daysMap = spanishDaysMap)
        assertEquals("Mié-Dom", result)
    }

    @Test
    fun `GIVEN day range J-V WHEN resolveScheduleDayRange is called THEN result is Jue-Vie`() {
        val result = resolveScheduleDayRange(dayRange = "J-V", daysMap = spanishDaysMap)
        assertEquals("Jue-Vie", result)
    }

    @Test
    fun `GIVEN day range J-S WHEN resolveScheduleDayRange is called THEN result is Jue-Sab`() {
        val result = resolveScheduleDayRange(dayRange = "J-S", daysMap = spanishDaysMap)
        assertEquals("Jue-Sáb", result)
    }

    @Test
    fun `GIVEN day range J-D WHEN resolveScheduleDayRange is called THEN result is Jue-Dom`() {
        val result = resolveScheduleDayRange(dayRange = "J-D", daysMap = spanishDaysMap)
        assertEquals("Jue-Dom", result)
    }

    @Test
    fun `GIVEN day range V-S WHEN resolveScheduleDayRange is called THEN result is Vie-Sab`() {
        val result = resolveScheduleDayRange(dayRange = "V-S", daysMap = spanishDaysMap)
        assertEquals("Vie-Sáb", result)
    }

    @Test
    fun `GIVEN day range V-D WHEN resolveScheduleDayRange is called THEN result is Vie-Dom`() {
        val result = resolveScheduleDayRange(dayRange = "V-D", daysMap = spanishDaysMap)
        assertEquals("Vie-Dom", result)
    }

    @Test
    fun `GIVEN day range S-D WHEN resolveScheduleDayRange is called THEN result is Sab-Dom`() {
        val result = resolveScheduleDayRange(dayRange = "S-D", daysMap = spanishDaysMap)
        assertEquals("Sáb-Dom", result)
    }

    @Test
    fun `GIVEN single lowercase day token l WHEN resolveScheduleDayRange is called THEN result is Lun ignoring case`() {
        val result = resolveScheduleDayRange(dayRange = "l", daysMap = spanishDaysMap)
        assertEquals("Lun", result)
    }

    @Test
    fun `GIVEN single day token D WHEN resolveScheduleDayRange is called THEN result is Dom`() {
        val result = resolveScheduleDayRange(dayRange = "D", daysMap = spanishDaysMap)
        assertEquals("Dom", result)
    }

    @Test
    fun `GIVEN day range with unknown tokens Z-W WHEN resolveScheduleDayRange is called THEN the original string is returned as-is`() {
        val result = resolveScheduleDayRange(dayRange = "Z-W", daysMap = spanishDaysMap)
        assertEquals("Z-W", result)
    }

    // endregion

    // region formatSchedulePure

    private val label24h = "Abierto 24h"
    private val daysMap = mapOf(
        "L" to "Lun",
        "M" to "Mar",
        "X" to "Mié",
        "J" to "Jue",
        "V" to "Vie",
        "S" to "Sáb",
        "D" to "Dom",
    )

    @Test
    fun `GIVEN schedule string contains 24H marker WHEN formatSchedulePure is called THEN the 24h label is returned`() {
        val result = formatSchedulePure(
            schedule = "L-D:00:00-24:00 (24H)",
            label24h = label24h,
            daysMap = daysMap,
        )
        assertEquals(label24h, result)
    }

    @Test
    fun `GIVEN schedule string contains lowercase 24h marker WHEN formatSchedulePure is called THEN the 24h label is returned`() {
        val result = formatSchedulePure(
            schedule = "L-D:00:00-24:00 (24h)",
            label24h = label24h,
            daysMap = daysMap,
        )
        assertEquals(label24h, result)
    }

    @Test
    fun `GIVEN single-segment schedule L-V 09-20 WHEN formatSchedulePure is called THEN result is the translated day range followed by the time range`() {
        val result = formatSchedulePure(
            schedule = "L-V:09:00-20:00",
            label24h = label24h,
            daysMap = daysMap,
        )
        assertEquals("Lun-Vie 09:00-20:00", result)
    }

    @Test
    fun `GIVEN two-segment schedule separated by semicolon WHEN formatSchedulePure is called THEN both segments are joined by a newline`() {
        val result = formatSchedulePure(
            schedule = "L-V:09:00-20:00;S:10:00-14:00",
            label24h = label24h,
            daysMap = daysMap,
        )
        assertEquals("Lun-Vie 09:00-20:00\nSáb 10:00-14:00", result)
    }

    @Test
    fun `GIVEN three-segment schedule separated by semicolons WHEN formatSchedulePure is called THEN all three segments are joined by newlines`() {
        val result = formatSchedulePure(
            schedule = "L-V:09:00-20:00;S:10:00-14:00;D:11:00-13:00",
            label24h = label24h,
            daysMap = daysMap,
        )
        assertEquals("Lun-Vie 09:00-20:00\nSáb 10:00-14:00\nDom 11:00-13:00", result)
    }

    @Test
    fun `GIVEN schedule segments with surrounding whitespace around semicolons WHEN formatSchedulePure is called THEN whitespace is trimmed and segments are correct`() {
        val result = formatSchedulePure(
            schedule = "L-V:09:00-20:00 ; S:10:00-14:00",
            label24h = label24h,
            daysMap = daysMap,
        )
        assertEquals("Lun-Vie 09:00-20:00\nSáb 10:00-14:00", result)
    }

    @Test
    fun `GIVEN schedule with a single day token S WHEN formatSchedulePure is called THEN result uses the translated single day name`() {
        val result = formatSchedulePure(
            schedule = "S:10:00-14:00",
            label24h = label24h,
            daysMap = daysMap,
        )
        assertEquals("Sáb 10:00-14:00", result)
    }

    @Test
    fun `GIVEN schedule with L-S range WHEN formatSchedulePure is called THEN result uses translated Lun-Sab range`() {
        val result = formatSchedulePure(
            schedule = "L-S:08:00-22:00",
            label24h = label24h,
            daysMap = daysMap,
        )
        assertEquals("Lun-Sáb 08:00-22:00", result)
    }

    // endregion
}
