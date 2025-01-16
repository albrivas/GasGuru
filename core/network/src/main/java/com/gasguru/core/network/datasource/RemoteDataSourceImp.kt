package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import com.gasguru.core.network.model.NetworkListPriceHistory
import com.gasguru.core.network.retrofit.ApiService
import javax.inject.Inject

class RemoteDataSourceImp @Inject constructor(
    private val api: ApiService,
) : RemoteDataSource {

    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> = tryCall {
        api.listFuelStations()
    }

    override suspend fun getPriceHistory(
        date: String,
        idMunicipality: String,
        idProduct: String,
    ): Either<NetworkError, NetworkListPriceHistory> = tryCall {
        api.priceHistory(date = date, idMunicipality = idMunicipality, idProduct = idProduct)
    }
}
