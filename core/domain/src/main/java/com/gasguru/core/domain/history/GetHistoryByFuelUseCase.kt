package com.gasguru.core.domain.history

import com.gasguru.core.data.repository.history.GetHistoryByFuelRepository
import com.gasguru.core.model.data.PriceHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetHistoryByFuelUseCase @Inject constructor(
    private val repository: GetHistoryByFuelRepository,
) {
    operator fun invoke(
        numberOfDays: Int = 15,
        date: LocalDate,
        idStation: Int,
        idMunicipality: String,
        idProduct: Int,
    ): Flow<List<PriceHistory>> =
        repository.getHistory(
            numberOfDays = numberOfDays,
            date = date,
            idStation = idStation,
            idMunicipality = idMunicipality,
            idProduct = idProduct
        )
}
