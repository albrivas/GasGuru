package com.albrivas.fuelpump.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelStationBrandsType
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.PriceCategory
import com.albrivas.fuelpump.core.uikit.icon.FuelStationIcons
import com.albrivas.fuelpump.core.uikit.theme.PriceCheap
import com.albrivas.fuelpump.core.uikit.theme.PriceExpensive
import com.albrivas.fuelpump.core.uikit.theme.PriceNone
import com.albrivas.fuelpump.core.uikit.theme.PriceNormal
import com.albrivas.fuelpump.core.uikit.theme.primaryContainerLight
import com.albrivas.fuelpump.core.uikit.theme.secondaryContainerLight
import com.albrivas.fuelpump.core.uikit.theme.secondaryLight
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun FuelType.translation() = when (this) {
    FuelType.GASOLINE_95 -> R.string.gasoline_95
    FuelType.GASOLINE_98 -> R.string.gasoline_98
    FuelType.DIESEL -> R.string.diesel
    FuelType.DIESEL_PLUS -> R.string.diesel_plus
    FuelType.ELECTRIC -> R.string.electric
}

fun Int.toFuelType() = when (this) {
    R.string.gasoline_95 -> FuelType.GASOLINE_95
    R.string.gasoline_98 -> FuelType.GASOLINE_98
    R.string.diesel -> FuelType.DIESEL
    R.string.diesel_plus -> FuelType.DIESEL_PLUS
    R.string.electric -> FuelType.ELECTRIC
    else -> FuelType.GASOLINE_95
}

fun FuelStationBrandsType.toBrandStationIcon() = when (this) {
    FuelStationBrandsType.ALCAMPO -> FuelStationIcons.Alcampo
    FuelStationBrandsType.BALLENOIL -> FuelStationIcons.Ballenoil
    FuelStationBrandsType.BONAREA -> FuelStationIcons.Bonarea
    FuelStationBrandsType.BP -> FuelStationIcons.Bp
    FuelStationBrandsType.CARREFOUR -> FuelStationIcons.Carrefour
    FuelStationBrandsType.CEPSA -> FuelStationIcons.Cepsa
    FuelStationBrandsType.DISA -> FuelStationIcons.Disa
    FuelStationBrandsType.ECLERC -> FuelStationIcons.Eclerc
    FuelStationBrandsType.ELECLERC -> FuelStationIcons.Eleclerc
    FuelStationBrandsType.EROSKI -> FuelStationIcons.Eroski
    FuelStationBrandsType.ESSO -> FuelStationIcons.Esso
    FuelStationBrandsType.GALP -> FuelStationIcons.Galp
    FuelStationBrandsType.MAKRO -> FuelStationIcons.Makro
    FuelStationBrandsType.MEROIL -> FuelStationIcons.Meroil
    FuelStationBrandsType.PETRONOR -> FuelStationIcons.Petronor
    FuelStationBrandsType.REPSOL -> FuelStationIcons.Repsol
    FuelStationBrandsType.SHELL -> FuelStationIcons.Shell
    FuelStationBrandsType.TEXACO -> FuelStationIcons.Texaco
    FuelStationBrandsType.TGAS -> FuelStationIcons.Tgas
    FuelStationBrandsType.ZOLOIL -> FuelStationIcons.Tgas
    FuelStationBrandsType.PC -> FuelStationIcons.Pcan
    FuelStationBrandsType.Q8 -> FuelStationIcons.Q8
    FuelStationBrandsType.SILVER_FUEL -> FuelStationIcons.SilverFuel
    FuelStationBrandsType.AZUL_OIL -> FuelStationIcons.AzulOil
    FuelStationBrandsType.FARRUCO -> FuelStationIcons.Farruco
    FuelStationBrandsType.REPOSTAR -> FuelStationIcons.Repostar
    FuelStationBrandsType.UNKOWN -> FuelStationIcons.Uknown
}

fun PriceCategory.toColor() = when (this) {
    PriceCategory.NONE -> secondaryLight
    PriceCategory.CHEAP -> PriceCheap
    PriceCategory.NORMAL -> PriceNormal
    PriceCategory.EXPENSIVE -> PriceExpensive
}

fun FuelType?.getPrice(fuelStation: FuelStation) = when (this) {
    FuelType.GASOLINE_95 -> "${fuelStation.priceGasoline95E5}"
    FuelType.GASOLINE_98 -> "${fuelStation.priceGasoline98E5}"
    FuelType.DIESEL -> "${fuelStation.priceGasoilA}"
    FuelType.DIESEL_PLUS -> "${fuelStation.priceGasoilPremium}"
    FuelType.ELECTRIC -> "0.0"
    null -> "0.0"
}

@Composable
fun FuelStation.getFuelPrices(): List<Pair<String, Double>> {
    return mapOf(
        R.string.diesel to priceGasoilA,
        R.string.diesel_plus to priceGasoilPremium,
        R.string.gasoline_95 to priceGasoline95E5,
        R.string.gasoline_98 to priceGasoline98E5
    )
        .filterValues { it > 0.0 }
        .map { Pair(stringResource(id = it.key), it.value) }
}

const val FORMAT_TIME_24H = "HH:mm"
const val END_OF_DAY_TIME = "23:59"
const val SCHEDULE_24H = "L-D: 24H"

@Suppress("ReturnCount")
fun FuelStation.isStationOpen(): Boolean {
    if (schedule.trim().uppercase(Locale.ROOT) == SCHEDULE_24H) {
        return true
    }

    val now = ZonedDateTime.now()
    val dayOfWeek = now.dayOfWeek
    val currentTime = now.toLocalTime()

    val scheduleParts = schedule.split(";")
    for (part in scheduleParts) {
        val dayAndTime = part.trim().split(":")
        if (dayAndTime.size != 2) {
            continue
        }
        val days = dayAndTime[0].trim()
        val times = dayAndTime[1].trim().split("-")

        if (isDayMatched(days, dayOfWeek) && isTimeInRange(times, currentTime)) {
            return true
        }
    }

    return false
}

private fun isDayMatched(days: String, currentDay: DayOfWeek): Boolean {
    val dayRange = days.split("-")
    val startDay = dayOfWeekMap[dayRange[0].uppercase(Locale.ROOT)]
        ?: throw IllegalArgumentException("Día de inicio inválido: ${dayRange[0]}")
    val endDay = if (dayRange.size > 1) {
        dayOfWeekMap[dayRange[1].uppercase(Locale.ROOT)]
            ?: throw IllegalArgumentException("Día de fin inválido: ${dayRange[1]}")
    } else {
        startDay
    }

    return currentDay in startDay..endDay
}

const val MIN_TIME_LENGTH = 5

private fun isTimeInRange(times: List<String>, currentTime: LocalTime): Boolean {
    val startTime =
        LocalTime.parse(times[0].padEnd(MIN_TIME_LENGTH, '0'), DateTimeFormatter.ofPattern(FORMAT_TIME_24H))
    val endTime = if (times.size > 1) {
        LocalTime.parse(times[1].padEnd(MIN_TIME_LENGTH, '0'), DateTimeFormatter.ofPattern(FORMAT_TIME_24H))
    } else {
        LocalTime.parse(END_OF_DAY_TIME, DateTimeFormatter.ofPattern(FORMAT_TIME_24H))
    }

    return (currentTime.isAfter(startTime) || currentTime == startTime) &&
        !currentTime.isAfter(endTime)
}

private val dayOfWeekMap = mapOf(
    "L" to DayOfWeek.MONDAY,
    "M" to DayOfWeek.TUESDAY,
    "X" to DayOfWeek.WEDNESDAY,
    "J" to DayOfWeek.THURSDAY,
    "V" to DayOfWeek.FRIDAY,
    "S" to DayOfWeek.SATURDAY,
    "D" to DayOfWeek.SUNDAY
)
