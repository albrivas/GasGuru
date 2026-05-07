package com.gasguru.feature.detail_station

import androidx.compose.runtime.Composable
import com.gasguru.feature.detail_station.generated.resources.Res
import com.gasguru.feature.detail_station.generated.resources.friday_short
import com.gasguru.feature.detail_station.generated.resources.monday_short
import com.gasguru.feature.detail_station.generated.resources.open_24h
import com.gasguru.feature.detail_station.generated.resources.saturday_short
import com.gasguru.feature.detail_station.generated.resources.sunday_short
import com.gasguru.feature.detail_station.generated.resources.thursday_short
import com.gasguru.feature.detail_station.generated.resources.tuesday_short
import com.gasguru.feature.detail_station.generated.resources.update_day_ago
import com.gasguru.feature.detail_station.generated.resources.update_days_ago
import com.gasguru.feature.detail_station.generated.resources.update_hour_ago
import com.gasguru.feature.detail_station.generated.resources.update_hours_ago
import com.gasguru.feature.detail_station.generated.resources.update_long_ago
import com.gasguru.feature.detail_station.generated.resources.update_minute_ago
import com.gasguru.feature.detail_station.generated.resources.update_minutes_ago
import com.gasguru.feature.detail_station.generated.resources.update_now
import com.gasguru.feature.detail_station.generated.resources.wednesday_short
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

private const val ONE_MINUTE_MS = 60_000L
private const val ONE_HOUR_MS = 3_600_000L
private const val ONE_DAY_MS = 86_400_000L
private const val ONE_WEEK_MS = 604_800_000L

@Composable
fun getTimeElapsedString(timestamp: Long): String {
    val currentTime = Clock.System.now().toEpochMilliseconds()
    val timeDiff = abs(currentTime - timestamp)

    return when {
        timeDiff < ONE_MINUTE_MS -> stringResource(Res.string.update_now)
        timeDiff < ONE_HOUR_MS -> {
            val minutes = timeDiff / ONE_MINUTE_MS
            if (minutes == 1L) {
                stringResource(Res.string.update_minute_ago)
            } else {
                stringResource(Res.string.update_minutes_ago, minutes)
            }
        }
        timeDiff < ONE_DAY_MS -> {
            val hours = timeDiff / ONE_HOUR_MS
            if (hours == 1L) {
                stringResource(Res.string.update_hour_ago)
            } else {
                stringResource(Res.string.update_hours_ago, hours)
            }
        }
        timeDiff < ONE_WEEK_MS -> {
            val days = timeDiff / ONE_DAY_MS
            if (days == 1L) {
                stringResource(Res.string.update_day_ago)
            } else {
                stringResource(Res.string.update_days_ago, days)
            }
        }
        else -> stringResource(Res.string.update_long_ago)
    }
}

@Composable
fun formatSchedule(schedule: String): String {
    return when {
        schedule.contains("24H", ignoreCase = true) -> stringResource(Res.string.open_24h)
        else -> {
            val daysOfWeek = mapOf(
                "L" to stringResource(Res.string.monday_short),
                "M" to stringResource(Res.string.tuesday_short),
                "X" to stringResource(Res.string.wednesday_short),
                "J" to stringResource(Res.string.thursday_short),
                "V" to stringResource(Res.string.friday_short),
                "S" to stringResource(Res.string.saturday_short),
                "D" to stringResource(Res.string.sunday_short),
            )

            val parts = schedule.split(";").map { it.trim() }
            val formattedParts = parts.map { part ->
                val dayRange = part.substringBefore(":")
                val timeRange = part.substringAfter(":")

                val formattedDays = when (dayRange.uppercase()) {
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
                    "L", "M", "X", "J", "V", "S", "D" -> daysOfWeek[dayRange.uppercase()].toString()
                    else -> dayRange
                }

                "$formattedDays $timeRange"
            }.filter { it.isNotBlank() }

            formattedParts.joinToString(separator = "\n")
        }
    }
}
