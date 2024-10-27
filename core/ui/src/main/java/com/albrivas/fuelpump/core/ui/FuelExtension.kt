package com.albrivas.fuelpump.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelStationBrandsType
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.PriceCategory
import com.albrivas.fuelpump.core.uikit.components.price.PriceItemModel
import com.albrivas.fuelpump.core.uikit.icon.FuelStationIcons
import com.albrivas.fuelpump.core.uikit.theme.AccentGreen
import com.albrivas.fuelpump.core.uikit.theme.AccentOrange
import com.albrivas.fuelpump.core.uikit.theme.AccentRed
import com.albrivas.fuelpump.core.uikit.theme.secondaryLight
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.albrivas.fuelpump.core.uikit.R as RUikit

fun FuelType.translation() = when (this) {
    FuelType.GASOLINE_95 -> R.string.gasoline_95
    FuelType.GASOLINE_98 -> R.string.gasoline_98
    FuelType.DIESEL -> R.string.diesel
    FuelType.DIESEL_PLUS -> R.string.diesel_plus
}

fun Int.toFuelType() = when (this) {
    R.string.gasoline_95 -> FuelType.GASOLINE_95
    R.string.gasoline_98 -> FuelType.GASOLINE_98
    R.string.diesel -> FuelType.DIESEL
    R.string.diesel_plus -> FuelType.DIESEL_PLUS
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
    PriceCategory.CHEAP -> AccentGreen
    PriceCategory.NORMAL -> AccentOrange
    PriceCategory.EXPENSIVE -> AccentRed
}

fun FuelType?.getPrice(fuelStation: FuelStation) = when (this) {
    FuelType.GASOLINE_95 -> "${fuelStation.priceGasoline95E5}"
    FuelType.GASOLINE_98 -> "${fuelStation.priceGasoline98E5}"
    FuelType.DIESEL -> "${fuelStation.priceGasoilA}"
    FuelType.DIESEL_PLUS -> "${fuelStation.priceGasoilPremium}"
    null -> "0.0"
}

fun FuelType.getIcon() = when (this) {
    FuelType.GASOLINE_95 -> RUikit.drawable.ic_gasoline_95
    FuelType.GASOLINE_98 -> RUikit.drawable.ic_gasoline_98
    FuelType.DIESEL -> RUikit.drawable.ic_diesel
    FuelType.DIESEL_PLUS -> RUikit.drawable.ic_diesel_plus
}

@Composable
fun FuelStation.getFuelPriceItems(): List<PriceItemModel> {
    return listOf(
        PriceItemModel(
            icon = RUikit.drawable.ic_diesel,
            fuelName = stringResource(id = R.string.diesel),
            price = "$priceGasoilA €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_diesel_plus,
            fuelName = stringResource(id = R.string.diesel_plus),
            price = "$priceGasoilPremium €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_gasoline_95,
            fuelName = stringResource(id = R.string.gasoline_95),
            price = "$priceGasoline95E5 €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_gasoline_98,
            fuelName = stringResource(id = R.string.gasoline_98),
            price = "$priceGasoline98E5 €/L"
        )
    ).filter { it.price > "0.0 €/L" }
}

const val FORMAT_TIME_24H = "HH:mm"
const val SCHEDULE_24H = "L-D: 24H"

@Suppress("ReturnCount")
fun FuelStation.isStationOpen(): Boolean {
    val now = ZonedDateTime.now()
    val currentDay = now.dayOfWeek
    val currentTime = now.toLocalTime()

    if (schedule.trim().uppercase(Locale.ROOT) == SCHEDULE_24H) {
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

            if (isDayMatched(days, currentDay) && isTimeInRange(startTime, endTime, currentTime)) {
                return true
            }
        }
    }

    return false
}

fun isTimeInRange(startTimeStr: String, endTimeStr: String, currentTime: LocalTime): Boolean {
    val formatter = DateTimeFormatter.ofPattern(FORMAT_TIME_24H)

    val startTime = LocalTime.parse(startTimeStr, formatter)
    val endTime = LocalTime.parse(endTimeStr, formatter)

    if (endTime.isAfter(startTime) || endTime == startTime) {
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime)
    }

    return currentTime.isAfter(startTime) || currentTime.isBefore(endTime)
}

fun isDayMatched(days: String, currentDay: DayOfWeek): Boolean {
    return when (days) {
        "L-D" -> true
        "L-V" -> currentDay.value in 1..5
        "L-S" -> currentDay.value in 1..6
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
