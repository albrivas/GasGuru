package com.gasguru.feature.detail_station

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@Composable
fun getTimeElapsedString(timestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val timeDiff = abs(currentTime - timestamp)

    return when {
        timeDiff < TimeUnit.MINUTES.toMillis(1) -> stringResource(id = R.string.update_now)
        timeDiff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff)
            if (minutes == 1L) {
                stringResource(id = R.string.update_minute_ago)
            } else {
                stringResource(id = R.string.update_minutes_ago, minutes)
            }
        }
        timeDiff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(timeDiff)
            if (hours == 1L) {
                stringResource(id = R.string.update_hour_ago)
            } else {
                stringResource(id = R.string.update_hours_ago, hours)
            }
        }
        timeDiff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(timeDiff)
            if (days == 1L) {
                stringResource(id = R.string.update_day_ago)
            } else {
                stringResource(id = R.string.update_days_ago, days)
            }
        }
        else -> {
            stringResource(id = R.string.update_long_ago)
        }
    }
}

@Composable
fun formatSchedule(schedule: String): String {
    return when {
        schedule.contains("24H", ignoreCase = true) -> { stringResource(R.string.open_24h) }
        else -> {
            val daysOfWeek = mapOf(
                "L" to stringResource(R.string.monday_short),
                "M" to stringResource(R.string.tuesday_short),
                "X" to stringResource(R.string.wednesday_short),
                "J" to stringResource(R.string.thursday_short),
                "V" to stringResource(R.string.friday_short),
                "S" to stringResource(R.string.saturday_short),
                "D" to stringResource(R.string.sunday_short)
            )

            val parts = schedule.split(";").map { it.trim() }
            val formattedParts = parts.map { part ->
                val dayRange = part.substringBefore(":")
                val timeRange = part.substringAfter(":")

                val formattedDays = when (dayRange.uppercase(java.util.Locale.getDefault())) {
                    "L-V" -> "${daysOfWeek["L"]}-${daysOfWeek["V"]}"
                    "L-S" -> "${daysOfWeek["L"]}-${daysOfWeek["S"]}"
                    "L-D" -> "${daysOfWeek["L"]}-${daysOfWeek["D"]}"
                    "M-V" -> "${daysOfWeek["M"]}-${daysOfWeek["V"]}"
                    "M-S" -> "${daysOfWeek["M"]}-${daysOfWeek["S"]}"
                    "M-D" -> "${daysOfWeek["M"]}-${daysOfWeek["D"]}"
                    "X-V" -> "${daysOfWeek["X"]}-${daysOfWeek["V"]}"
                    "X-S" -> "${daysOfWeek["X"]}-${daysOfWeek["S"]}"
                    "X-D" -> "${daysOfWeek["X"]}-${daysOfWeek["D"]}"
                    "J-V" -> "${daysOfWeek["J"]}-${daysOfWeek["V"]}"
                    "J-S" -> "${daysOfWeek["J"]}-${daysOfWeek["S"]}"
                    "J-D" -> "${daysOfWeek["J"]}-${daysOfWeek["D"]}"
                    "V-S" -> "${daysOfWeek["V"]}-${daysOfWeek["S"]}"
                    "V-D" -> "${daysOfWeek["V"]}-${daysOfWeek["D"]}"
                    "S-D" -> "${daysOfWeek["S"]}-${daysOfWeek["D"]}"
                    "L", "M", "X", "J", "V", "S", "D" -> daysOfWeek[
                        dayRange.uppercase(
                            java.util.Locale.getDefault()
                        )
                    ].toString()
                    else -> dayRange // Default to original string if not a recognized format
                }

                "$formattedDays $timeRange"
            }.filter { it.isNotBlank() } // Filter out empty strings

            formattedParts.joinToString(separator = "\n")
        }
    }
}
