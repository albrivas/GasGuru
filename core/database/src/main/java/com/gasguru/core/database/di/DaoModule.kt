package com.gasguru.core.database.di

import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.dao.FavoriteStationDao
import com.gasguru.core.database.dao.FilterDao
import com.gasguru.core.database.dao.FuelStationDao
import com.gasguru.core.database.dao.PriceAlertDao
import com.gasguru.core.database.dao.RecentSearchQueryDao
import com.gasguru.core.database.dao.UserDataDao
import org.koin.dsl.module

val daoModule = module {
    single<FuelStationDao> { get<GasGuruDatabase>().fuelStationDao() }
    single<UserDataDao> { get<GasGuruDatabase>().userDataDao() }
    single<RecentSearchQueryDao> { get<GasGuruDatabase>().recentDao() }
    single<FilterDao> { get<GasGuruDatabase>().filterDao() }
    single<FavoriteStationDao> { get<GasGuruDatabase>().favoriteStationDao() }
    single<PriceAlertDao> { get<GasGuruDatabase>().priceAlertDao() }
}
