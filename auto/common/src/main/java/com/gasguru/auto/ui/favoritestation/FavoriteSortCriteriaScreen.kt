package com.gasguru.auto.ui.favoritestation

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.gasguru.auto.analytics.trackAutoFavoriteSortSelected
import com.gasguru.auto.common.R
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.ui.sort.StationSortCriteria
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.gasguru.core.ui.R as CoreUiR

// No stored state: sorting never persists across Android Auto sessions.
class FavoriteSortCriteriaScreen(carContext: CarContext) : Screen(carContext), KoinComponent {

    private val analyticsHelper: AnalyticsHelper by inject()

    private fun createSortOptions(): ItemList {
        val items = ItemList.Builder()

        items.addItem(sortOptionRow(titleRes = R.string.sort_by_price, criteria = StationSortCriteria.PRICE))
        items.addItem(sortOptionRow(titleRes = R.string.sort_by_distance, criteria = StationSortCriteria.DISTANCE))
        items.addItem(sortOptionRow(titleRes = R.string.sort_full_list, criteria = StationSortCriteria.NONE))

        return items.build()
    }

    private fun sortOptionRow(titleRes: Int, criteria: StationSortCriteria): Row =
        Row.Builder()
            .setTitle(carContext.getString(titleRes))
            .setBrowsable(true)
            .setOnClickListener {
                analyticsHelper.trackAutoFavoriteSortSelected(criteria = criteria.name)
                screenManager.push(FavoriteStationsScreen(carContext = carContext, sortCriteria = criteria))
            }
            .build()

    override fun onGetTemplate(): Template {
        val builder = PlaceListMapTemplate
            .Builder()
            .setTitle(carContext.getString(CoreUiR.string.favorites))
            .setHeaderAction(Action.BACK)

        builder.setItemList(createSortOptions())
        builder.setCurrentLocationEnabled(true)

        return builder.build()
    }
}
