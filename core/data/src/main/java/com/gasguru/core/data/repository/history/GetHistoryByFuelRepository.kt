package com.gasguru.core.data.repository.history

import com.gasguru.core.model.data.PriceHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

fun interface GetHistoryByFuelRepository {
    fun getHistory(
        numberOfDays: Int,
        date: LocalDate,
        idStation: Int,
        idMunicipality: String,
        idProduct: Int,
    ): Flow<List<PriceHistory>>
}
