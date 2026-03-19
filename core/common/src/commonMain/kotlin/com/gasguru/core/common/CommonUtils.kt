package com.gasguru.core.common

import com.gasguru.core.model.data.FuelStation
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime

object CommonUtils {

    @Suppress("ReturnCount")
    fun FuelStation.isStationOpen(
        now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    ): Boolean {
        val currentDay = now.dayOfWeek
        val currentTime = now.time

        if (schedule.trim().uppercase() == "L-D: 24H") {
            return true
        }

        val scheduleParts = schedule.split(";")
        for (part in scheduleParts) {
            val regex = Regex("""([LMXJVSD-]+):\s*([0-9]{2}:[0-9]{2})-([0-9]{2}:[0-9]{2})""")
            val matchResult = regex.find(part.trim())

            if (matchResult != null) {
                val days = matchResult.groupValues[1]
                val startTime = matchResult.groupValues[2]
                val endTime = matchResult.groupValues[3]

                if (isDayMatched(days = days, currentDay = currentDay) &&
                    isTimeInRange(startTimeStr = startTime, endTimeStr = endTime, currentTime = currentTime)
                ) {
                    return true
                }
            }
        }

        return false
    }

    private fun isTimeInRange(startTimeStr: String, endTimeStr: String, currentTime: LocalTime): Boolean {
        val (startHour, startMinute) = startTimeStr.split(":").map { it.toInt() }
        val (endHour, endMinute) = endTimeStr.split(":").map { it.toInt() }
        val startTime = LocalTime(hour = startHour, minute = startMinute)
        val endTime = LocalTime(hour = endHour, minute = endMinute)

        return if (endTime >= startTime) {
            currentTime > startTime && currentTime < endTime
        } else {
            currentTime > startTime || currentTime < endTime
        }
    }

    private fun isDayMatched(days: String, currentDay: DayOfWeek): Boolean {
        return when (days) {
            "L-D" -> true
            "L-V" -> currentDay.isoDayNumber in 1..5
            "L-S" -> currentDay.isoDayNumber in 1..6
            "L" -> currentDay == DayOfWeek.MONDAY
            "M" -> currentDay == DayOfWeek.TUESDAY
            "X" -> currentDay == DayOfWeek.WEDNESDAY
            "J" -> currentDay == DayOfWeek.THURSDAY
            "V" -> currentDay == DayOfWeek.FRIDAY
            "S" -> currentDay == DayOfWeek.SATURDAY
            "D" -> currentDay == DayOfWeek.SUNDAY
            else -> false
        }
    }
}
