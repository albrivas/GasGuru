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
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.time.Clock

internal const val ONE_MINUTE_MS = 60_000L
internal const val ONE_HOUR_MS = 3_600_000L
internal const val ONE_DAY_MS = 86_400_000L
internal const val ONE_WEEK_MS = 604_800_000L

/**
 * Sealed class representing the category of elapsed time.
 * Extracted from the @Composable to allow pure-logic unit testing.
 */
internal sealed class TimeElapsedCategory {
    data object Now : TimeElapsedCategory()
    data class OneMinute(val minutes: Long) : TimeElapsedCategory()
    data class Minutes(val minutes: Long) : TimeElapsedCategory()
    data class OneHour(val hours: Long) : TimeElapsedCategory()
    data class Hours(val hours: Long) : TimeElapsedCategory()
    data class OneDay(val days: Long) : TimeElapsedCategory()
    data class Days(val days: Long) : TimeElapsedCategory()
    data object LongAgo : TimeElapsedCategory()
}

/**
 * Pure function that classifies a time diff in milliseconds into a [TimeElapsedCategory].
 * Testable without a Compose host.
 */
internal fun classifyTimeDiff(timeDiff: Long): TimeElapsedCategory = when {
    timeDiff < ONE_MINUTE_MS -> TimeElapsedCategory.Now
    timeDiff < ONE_HOUR_MS -> {
        val minutes = timeDiff / ONE_MINUTE_MS
        if (minutes == 1L) TimeElapsedCategory.OneMinute(minutes) else TimeElapsedCategory.Minutes(minutes)
    }
    timeDiff < ONE_DAY_MS -> {
        val hours = timeDiff / ONE_HOUR_MS
        if (hours == 1L) TimeElapsedCategory.OneHour(hours) else TimeElapsedCategory.Hours(hours)
    }
    timeDiff < ONE_WEEK_MS -> {
        val days = timeDiff / ONE_DAY_MS
        if (days == 1L) TimeElapsedCategory.OneDay(days) else TimeElapsedCategory.Days(days)
    }
    else -> TimeElapsedCategory.LongAgo
}

/**
 * Pure function that resolves a single day-range token (e.g. "L-V") using an already-resolved
 * days map. Testable without a Compose host.
 */
internal fun resolveScheduleDayRange(dayRange: String, daysMap: Map<String, String>): String =
    when (dayRange.uppercase()) {
        "L-V" -> "${daysMap["L"]}-${daysMap["V"]}"
        "L-S" -> "${daysMap["L"]}-${daysMap["S"]}"
        "L-D" -> "${daysMap["L"]}-${daysMap["D"]}"
        "M-V" -> "${daysMap["M"]}-${daysMap["V"]}"
        "M-S" -> "${daysMap["M"]}-${daysMap["S"]}"
        "M-D" -> "${daysMap["M"]}-${daysMap["D"]}"
        "X-V" -> "${daysMap["X"]}-${daysMap["V"]}"
        "X-S" -> "${daysMap["X"]}-${daysMap["S"]}"
        "X-D" -> "${daysMap["X"]}-${daysMap["D"]}"
        "J-V" -> "${daysMap["J"]}-${daysMap["V"]}"
        "J-S" -> "${daysMap["J"]}-${daysMap["S"]}"
        "J-D" -> "${daysMap["J"]}-${daysMap["D"]}"
        "V-S" -> "${daysMap["V"]}-${daysMap["S"]}"
        "V-D" -> "${daysMap["V"]}-${daysMap["D"]}"
        "S-D" -> "${daysMap["S"]}-${daysMap["D"]}"
        "L", "M", "X", "J", "V", "S", "D" -> daysMap[dayRange.uppercase()].toString()
        else -> dayRange
    }

/**
 * Pure function that formats a schedule string using already-resolved day names and a 24h label.
 * Testable without a Compose host.
 */
internal fun formatSchedulePure(
    schedule: String,
    label24h: String,
    daysMap: Map<String, String>,
): String {
    if (schedule.contains("24H", ignoreCase = true)) return label24h

    val parts = schedule.split(";").map { it.trim() }
    val formattedParts = parts.map { part ->
        val dayRange = part.substringBefore(":")
        val timeRange = part.substringAfter(":")
        val formattedDays = resolveScheduleDayRange(dayRange = dayRange, daysMap = daysMap)
        "$formattedDays $timeRange"
    }.filter { it.isNotBlank() }

    return formattedParts.joinToString(separator = "\n")
}

@Composable
fun getTimeElapsedString(timestamp: Long): String {
    val currentTime = Clock.System.now().toEpochMilliseconds()
    val timeDiff = abs(currentTime - timestamp)

    return when (val category = classifyTimeDiff(timeDiff)) {
        is TimeElapsedCategory.Now -> stringResource(Res.string.update_now)
        is TimeElapsedCategory.OneMinute -> stringResource(Res.string.update_minute_ago)
        is TimeElapsedCategory.Minutes -> stringResource(Res.string.update_minutes_ago, category.minutes)
        is TimeElapsedCategory.OneHour -> stringResource(Res.string.update_hour_ago)
        is TimeElapsedCategory.Hours -> stringResource(Res.string.update_hours_ago, category.hours)
        is TimeElapsedCategory.OneDay -> stringResource(Res.string.update_day_ago)
        is TimeElapsedCategory.Days -> stringResource(Res.string.update_days_ago, category.days)
        is TimeElapsedCategory.LongAgo -> stringResource(Res.string.update_long_ago)
    }
}

@Composable
fun formatSchedule(schedule: String): String {
    val daysMap = mapOf(
        "L" to stringResource(Res.string.monday_short),
        "M" to stringResource(Res.string.tuesday_short),
        "X" to stringResource(Res.string.wednesday_short),
        "J" to stringResource(Res.string.thursday_short),
        "V" to stringResource(Res.string.friday_short),
        "S" to stringResource(Res.string.saturday_short),
        "D" to stringResource(Res.string.sunday_short),
    )
    return formatSchedulePure(
        schedule = schedule,
        label24h = stringResource(Res.string.open_24h),
        daysMap = daysMap,
    )
}
