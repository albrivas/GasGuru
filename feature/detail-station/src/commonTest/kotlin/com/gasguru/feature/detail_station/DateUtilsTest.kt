package com.gasguru.feature.detail_station

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("DateUtils pure functions")
class DateUtilsTest {

    // region classifyTimeDiff

    @Nested
    @DisplayName("classifyTimeDiff")
    inner class ClassifyTimeDiff {

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of 0 ms
            WHEN classifyTimeDiff is called
            THEN returns Now
            """
        )
        fun zeroMillisecondsReturnsNow() {
            val result = classifyTimeDiff(timeDiff = 0L)
            assertEquals(TimeElapsedCategory.Now, result)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff just below one minute (59_999 ms)
            WHEN classifyTimeDiff is called
            THEN returns Now
            """
        )
        fun justBelowOneMinuteReturnsNow() {
            val result = classifyTimeDiff(timeDiff = ONE_MINUTE_MS - 1L)
            assertEquals(TimeElapsedCategory.Now, result)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of exactly one minute (60_000 ms)
            WHEN classifyTimeDiff is called
            THEN returns OneMinute with minutes=1
            """
        )
        fun exactlyOneMinuteReturnsOneMinute() {
            val result = classifyTimeDiff(timeDiff = ONE_MINUTE_MS)
            assertInstanceOf(TimeElapsedCategory.OneMinute::class.java, result)
            assertEquals(1L, (result as TimeElapsedCategory.OneMinute).minutes)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of 5 minutes
            WHEN classifyTimeDiff is called
            THEN returns Minutes with minutes=5
            """
        )
        fun fiveMinutesReturnsMinutes() {
            val result = classifyTimeDiff(timeDiff = 5 * ONE_MINUTE_MS)
            assertInstanceOf(TimeElapsedCategory.Minutes::class.java, result)
            assertEquals(5L, (result as TimeElapsedCategory.Minutes).minutes)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff just below one hour (3_599_999 ms)
            WHEN classifyTimeDiff is called
            THEN returns Minutes with minutes=59
            """
        )
        fun justBelowOneHourReturnsMinutes() {
            val result = classifyTimeDiff(timeDiff = ONE_HOUR_MS - 1L)
            assertInstanceOf(TimeElapsedCategory.Minutes::class.java, result)
            assertEquals(59L, (result as TimeElapsedCategory.Minutes).minutes)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of exactly one hour
            WHEN classifyTimeDiff is called
            THEN returns OneHour with hours=1
            """
        )
        fun exactlyOneHourReturnsOneHour() {
            val result = classifyTimeDiff(timeDiff = ONE_HOUR_MS)
            assertInstanceOf(TimeElapsedCategory.OneHour::class.java, result)
            assertEquals(1L, (result as TimeElapsedCategory.OneHour).hours)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of 3 hours
            WHEN classifyTimeDiff is called
            THEN returns Hours with hours=3
            """
        )
        fun threeHoursReturnsHours() {
            val result = classifyTimeDiff(timeDiff = 3 * ONE_HOUR_MS)
            assertInstanceOf(TimeElapsedCategory.Hours::class.java, result)
            assertEquals(3L, (result as TimeElapsedCategory.Hours).hours)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff just below one day
            WHEN classifyTimeDiff is called
            THEN returns Hours with hours=23
            """
        )
        fun justBelowOneDayReturnsHours() {
            val result = classifyTimeDiff(timeDiff = ONE_DAY_MS - 1L)
            assertInstanceOf(TimeElapsedCategory.Hours::class.java, result)
            assertEquals(23L, (result as TimeElapsedCategory.Hours).hours)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of exactly one day
            WHEN classifyTimeDiff is called
            THEN returns OneDay with days=1
            """
        )
        fun exactlyOneDayReturnsOneDay() {
            val result = classifyTimeDiff(timeDiff = ONE_DAY_MS)
            assertInstanceOf(TimeElapsedCategory.OneDay::class.java, result)
            assertEquals(1L, (result as TimeElapsedCategory.OneDay).days)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of 3 days
            WHEN classifyTimeDiff is called
            THEN returns Days with days=3
            """
        )
        fun threeDaysReturnsDays() {
            val result = classifyTimeDiff(timeDiff = 3 * ONE_DAY_MS)
            assertInstanceOf(TimeElapsedCategory.Days::class.java, result)
            assertEquals(3L, (result as TimeElapsedCategory.Days).days)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff just below one week
            WHEN classifyTimeDiff is called
            THEN returns Days with days=6
            """
        )
        fun justBelowOneWeekReturnsDays() {
            val result = classifyTimeDiff(timeDiff = ONE_WEEK_MS - 1L)
            assertInstanceOf(TimeElapsedCategory.Days::class.java, result)
            assertEquals(6L, (result as TimeElapsedCategory.Days).days)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of exactly one week
            WHEN classifyTimeDiff is called
            THEN returns LongAgo
            """
        )
        fun exactlyOneWeekReturnsLongAgo() {
            val result = classifyTimeDiff(timeDiff = ONE_WEEK_MS)
            assertEquals(TimeElapsedCategory.LongAgo, result)
        }

        @Test
        @DisplayName(
            """
            GIVEN timeDiff of one month
            WHEN classifyTimeDiff is called
            THEN returns LongAgo
            """
        )
        fun oneMonthReturnsLongAgo() {
            val result = classifyTimeDiff(timeDiff = 30 * ONE_DAY_MS)
            assertEquals(TimeElapsedCategory.LongAgo, result)
        }
    }

    // endregion

    // region resolveScheduleDayRange

    @Nested
    @DisplayName("resolveScheduleDayRange")
    inner class ResolveScheduleDayRange {

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
        @DisplayName(
            """
            GIVEN day range L-V
            WHEN resolveScheduleDayRange is called
            THEN returns Lun-Vie
            """
        )
        fun lToVReturnsLunViernes() {
            val result = resolveScheduleDayRange(dayRange = "L-V", daysMap = spanishDaysMap)
            assertEquals("Lun-Vie", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range L-S
            WHEN resolveScheduleDayRange is called
            THEN returns Lun-Sáb
            """
        )
        fun lToSReturnsLunSabado() {
            val result = resolveScheduleDayRange(dayRange = "L-S", daysMap = spanishDaysMap)
            assertEquals("Lun-Sáb", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range L-D
            WHEN resolveScheduleDayRange is called
            THEN returns Lun-Dom
            """
        )
        fun lToDReturnsLunDomingo() {
            val result = resolveScheduleDayRange(dayRange = "L-D", daysMap = spanishDaysMap)
            assertEquals("Lun-Dom", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range M-V
            WHEN resolveScheduleDayRange is called
            THEN returns Mar-Vie
            """
        )
        fun mToVReturnsMarViernes() {
            val result = resolveScheduleDayRange(dayRange = "M-V", daysMap = spanishDaysMap)
            assertEquals("Mar-Vie", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range M-S
            WHEN resolveScheduleDayRange is called
            THEN returns Mar-Sáb
            """
        )
        fun mToSReturnsMarSabado() {
            val result = resolveScheduleDayRange(dayRange = "M-S", daysMap = spanishDaysMap)
            assertEquals("Mar-Sáb", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range M-D
            WHEN resolveScheduleDayRange is called
            THEN returns Mar-Dom
            """
        )
        fun mToDReturnsMarDomingo() {
            val result = resolveScheduleDayRange(dayRange = "M-D", daysMap = spanishDaysMap)
            assertEquals("Mar-Dom", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range X-V
            WHEN resolveScheduleDayRange is called
            THEN returns Mié-Vie
            """
        )
        fun xToVReturnsMiercoles() {
            val result = resolveScheduleDayRange(dayRange = "X-V", daysMap = spanishDaysMap)
            assertEquals("Mié-Vie", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range X-S
            WHEN resolveScheduleDayRange is called
            THEN returns Mié-Sáb
            """
        )
        fun xToSReturnsMiercolesASabado() {
            val result = resolveScheduleDayRange(dayRange = "X-S", daysMap = spanishDaysMap)
            assertEquals("Mié-Sáb", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range X-D
            WHEN resolveScheduleDayRange is called
            THEN returns Mié-Dom
            """
        )
        fun xToDReturnsMiercolesADomingo() {
            val result = resolveScheduleDayRange(dayRange = "X-D", daysMap = spanishDaysMap)
            assertEquals("Mié-Dom", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range J-V
            WHEN resolveScheduleDayRange is called
            THEN returns Jue-Vie
            """
        )
        fun jToVReturnsJueVie() {
            val result = resolveScheduleDayRange(dayRange = "J-V", daysMap = spanishDaysMap)
            assertEquals("Jue-Vie", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range J-S
            WHEN resolveScheduleDayRange is called
            THEN returns Jue-Sáb
            """
        )
        fun jToSReturnsJueSab() {
            val result = resolveScheduleDayRange(dayRange = "J-S", daysMap = spanishDaysMap)
            assertEquals("Jue-Sáb", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range J-D
            WHEN resolveScheduleDayRange is called
            THEN returns Jue-Dom
            """
        )
        fun jToDReturnsJueDom() {
            val result = resolveScheduleDayRange(dayRange = "J-D", daysMap = spanishDaysMap)
            assertEquals("Jue-Dom", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range V-S
            WHEN resolveScheduleDayRange is called
            THEN returns Vie-Sáb
            """
        )
        fun vToSReturnsVieSab() {
            val result = resolveScheduleDayRange(dayRange = "V-S", daysMap = spanishDaysMap)
            assertEquals("Vie-Sáb", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range V-D
            WHEN resolveScheduleDayRange is called
            THEN returns Vie-Dom
            """
        )
        fun vToDReturnsVieDom() {
            val result = resolveScheduleDayRange(dayRange = "V-D", daysMap = spanishDaysMap)
            assertEquals("Vie-Dom", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN day range S-D
            WHEN resolveScheduleDayRange is called
            THEN returns Sáb-Dom
            """
        )
        fun sToDReturnsSabDom() {
            val result = resolveScheduleDayRange(dayRange = "S-D", daysMap = spanishDaysMap)
            assertEquals("Sáb-Dom", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN single day token L (lowercase)
            WHEN resolveScheduleDayRange is called
            THEN returns Lun (case-insensitive)
            """
        )
        fun singleDayLowercaseIsCaseInsensitive() {
            val result = resolveScheduleDayRange(dayRange = "l", daysMap = spanishDaysMap)
            assertEquals("Lun", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN single day token D
            WHEN resolveScheduleDayRange is called
            THEN returns Dom
            """
        )
        fun singleDayDReturnsDom() {
            val result = resolveScheduleDayRange(dayRange = "D", daysMap = spanishDaysMap)
            assertEquals("Dom", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN unknown day range token
            WHEN resolveScheduleDayRange is called
            THEN returns the original token unchanged
            """
        )
        fun unknownTokenReturnedAsIs() {
            val result = resolveScheduleDayRange(dayRange = "Z-W", daysMap = spanishDaysMap)
            assertEquals("Z-W", result)
        }
    }

    // endregion

    // region formatSchedulePure

    @Nested
    @DisplayName("formatSchedulePure")
    inner class FormatSchedulePure {

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
        @DisplayName(
            """
            GIVEN schedule containing 24H (uppercase)
            WHEN formatSchedulePure is called
            THEN returns the 24h label
            """
        )
        fun scheduleWith24HReturnsLabel() {
            val result = formatSchedulePure(
                schedule = "L-D:00:00-24:00 (24H)",
                label24h = label24h,
                daysMap = daysMap,
            )
            assertEquals(label24h, result)
        }

        @Test
        @DisplayName(
            """
            GIVEN schedule containing 24h (lowercase)
            WHEN formatSchedulePure is called
            THEN returns the 24h label (case-insensitive check)
            """
        )
        fun scheduleWith24hLowercaseReturnsLabel() {
            val result = formatSchedulePure(
                schedule = "L-D:00:00-24:00 (24h)",
                label24h = label24h,
                daysMap = daysMap,
            )
            assertEquals(label24h, result)
        }

        @Test
        @DisplayName(
            """
            GIVEN schedule with a single part L-V:09:00-20:00
            WHEN formatSchedulePure is called
            THEN returns Lun-Vie 09:00-20:00
            """
        )
        fun singlePartScheduleFormatsCorrectly() {
            val result = formatSchedulePure(
                schedule = "L-V:09:00-20:00",
                label24h = label24h,
                daysMap = daysMap,
            )
            assertEquals("Lun-Vie 09:00-20:00", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN schedule with two parts separated by semicolon
            WHEN formatSchedulePure is called
            THEN returns both parts joined by newline
            """
        )
        fun twoPartScheduleJoinedByNewline() {
            val result = formatSchedulePure(
                schedule = "L-V:09:00-20:00;S:10:00-14:00",
                label24h = label24h,
                daysMap = daysMap,
            )
            assertEquals("Lun-Vie 09:00-20:00\nSáb 10:00-14:00", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN schedule with three parts separated by semicolons
            WHEN formatSchedulePure is called
            THEN returns all three parts joined by newline
            """
        )
        fun threePartScheduleJoinedByNewline() {
            val result = formatSchedulePure(
                schedule = "L-V:09:00-20:00;S:10:00-14:00;D:11:00-13:00",
                label24h = label24h,
                daysMap = daysMap,
            )
            assertEquals("Lun-Vie 09:00-20:00\nSáb 10:00-14:00\nDom 11:00-13:00", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN schedule parts with surrounding whitespace
            WHEN formatSchedulePure is called
            THEN trims whitespace and formats correctly
            """
        )
        fun scheduleTrimsSurroundingWhitespace() {
            val result = formatSchedulePure(
                schedule = "L-V:09:00-20:00 ; S:10:00-14:00",
                label24h = label24h,
                daysMap = daysMap,
            )
            assertEquals("Lun-Vie 09:00-20:00\nSáb 10:00-14:00", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN schedule with a single day token
            WHEN formatSchedulePure is called
            THEN resolves that single day correctly
            """
        )
        fun singleDayTokenFormatsCorrectly() {
            val result = formatSchedulePure(
                schedule = "S:10:00-14:00",
                label24h = label24h,
                daysMap = daysMap,
            )
            assertEquals("Sáb 10:00-14:00", result)
        }

        @Test
        @DisplayName(
            """
            GIVEN schedule with L-S range
            WHEN formatSchedulePure is called
            THEN returns Lun-Sáb with time range
            """
        )
        fun lToSRangeFormatsCorrectly() {
            val result = formatSchedulePure(
                schedule = "L-S:08:00-22:00",
                label24h = label24h,
                daysMap = daysMap,
            )
            assertEquals("Lun-Sáb 08:00-22:00", result)
        }
    }

    // endregion
}
