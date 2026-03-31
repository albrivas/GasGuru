package com.gasguru.feature.widget.ui

import androidx.compose.runtime.Composable
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.feature.widget.model.FavoriteWidgetItemModel

private val previewStations = listOf(
    FavoriteWidgetItemModel(
        idServiceStation = 1,
        name = "Repsol Calle Mayor",
        direction = "Calle Mayor, 12, Madrid",
        formattedPrice = "1.459 €",
        priceCategory = PriceCategory.CHEAP,
    ),
    FavoriteWidgetItemModel(
        idServiceStation = 2,
        name = "BP Gran Vía",
        direction = "Gran Vía, 45, Madrid",
        formattedPrice = "1.529 €",
        priceCategory = PriceCategory.NORMAL,
    ),
    FavoriteWidgetItemModel(
        idServiceStation = 3,
        name = "Cepsa Paseo Castellana",
        direction = "P.º de la Castellana, 200",
        formattedPrice = "1.619 €",
        priceCategory = PriceCategory.EXPENSIVE,
    ),
)

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 250)
@Composable
private fun FavoriteStationsWidgetContentWithStationsPreview() {
    FavoriteStationsWidgetContent(stations = previewStations)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 250)
@Composable
private fun FavoriteStationsWidgetContentLoadingPreview() {
    FavoriteStationsWidgetContent(stations = null)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 250)
@Composable
private fun FavoriteStationsWidgetContentEmptyPreview() {
    FavoriteStationsWidgetContent(stations = emptyList())
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 320, heightDp = 250)
@Composable
private fun FavoriteStationsWidgetContentSingleStationPreview() {
    FavoriteStationsWidgetContent(stations = previewStations.take(1))
}
