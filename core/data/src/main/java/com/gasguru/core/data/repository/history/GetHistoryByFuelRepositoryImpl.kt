package com.gasguru.core.data.repository.history

import com.gasguru.core.common.IoDispatcher
import com.gasguru.core.model.data.PriceHistory
import com.gasguru.core.network.datasource.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetHistoryByFuelRepositoryImpl @Inject constructor(
    private val dataSource: RemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : GetHistoryByFuelRepository {
    override fun getHistory(
        numberOfDays: Int,
        date: LocalDate,
        idStation: String,
        idMunicipality: String,
        idProduct: String,
    ): Flow<List<PriceHistory>> {
        val dates = getLastDays(numberOfDays, date)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        return flow {
            val priceHistoryList = coroutineScope {
                dates.map { localDate ->
                    async {
                        val formattedDate = localDate.format(formatter)
                        val response = dataSource.getPriceHistory(
                            formattedDate,
                            idMunicipality,
                            idStation,
                            idProduct
                        )

                        response.fold(
                            ifLeft = { emptyList() },
                            ifRight = {
                                listOf(
                                    PriceHistory(
                                        date = formattedDate,
                                        price = it.price
                                    )
                                )
                            }
                        )
                    }
                }
            }.awaitAll()

            emit(priceHistoryList.flatten())
        }.flowOn(ioDispatcher)
    }
}

private fun getLastDays(numberOfDays: Int, date: LocalDate) =
    (0..numberOfDays).map { date.minusDays(it.toLong()) }.reversed()
