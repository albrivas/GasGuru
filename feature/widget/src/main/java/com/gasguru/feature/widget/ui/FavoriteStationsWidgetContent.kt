package com.gasguru.feature.widget.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.feature.widget.R
import com.gasguru.feature.widget.model.FavoriteWidgetItemModel
import com.gasguru.feature.widget.theme.WidgetColorScheme
import com.gasguru.feature.widget.theme.WidgetColors
import com.gasguru.feature.widget.theme.WidgetStyleBodyMedium
import com.gasguru.feature.widget.theme.WidgetStyleCaption
import com.gasguru.feature.widget.theme.WidgetStyleEmptySubtitle
import com.gasguru.feature.widget.theme.WidgetStyleEmptyTitle
import com.gasguru.feature.widget.theme.WidgetStyleHeader
import com.gasguru.feature.widget.theme.WidgetStylePrice

@Composable
fun FavoriteStationsWidgetContent(
    stations: List<FavoriteWidgetItemModel>,
    isCompact: Boolean = false,
) {
    GlanceTheme(colors = WidgetColorScheme.colors) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground),
        ) {
            WidgetHeader(isCompact = isCompact)
            if (stations.isEmpty()) {
                EmptyWidgetContent()
            } else {
                StationsListContent(
                    stations = stations,
                    isCompact = isCompact,
                )
            }
        }
    }
}

@Composable
private fun WidgetHeader(isCompact: Boolean) {
    val context = LocalContext. current
    val openAppIntent = Intent().apply {
        setClassName(context.packageName, "com.gasguru.MainActivity")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val verticalPadding = if (isCompact) 6.dp else 10.dp
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = verticalPadding)
            .clickable(actionStartActivity(openAppIntent)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = context.getString(R.string.widget_title),
            style = WidgetStyleHeader.copy(color = GlanceTheme.colors.onSurface),
        )
    }
}

@Composable
private fun EmptyWidgetContent() {
    val context = LocalContext.current
    val openAppIntent = Intent().apply {
        setClassName(context.packageName, "com.gasguru.MainActivity")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable(actionStartActivity(openAppIntent)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = context.getString(R.string.widget_empty_title),
            style = WidgetStyleEmptyTitle.copy(color = GlanceTheme.colors.onSurface),
        )
        Text(
            text = context.getString(R.string.widget_empty_subtitle),
            style = WidgetStyleEmptySubtitle.copy(color = GlanceTheme.colors.onSurfaceVariant),
        )
    }
}

@Composable
private fun StationsListContent(
    stations: List<FavoriteWidgetItemModel>,
    isCompact: Boolean,
) {
    LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
        items(stations) { stationItem ->
            StationWidgetItem(
                stationItem = stationItem,
                isCompact = isCompact,
            )
        }
    }
}

@Composable
private fun StationWidgetItem(
    stationItem: FavoriteWidgetItemModel,
    isCompact: Boolean,
) {
    val context = LocalContext.current
    val openDetailIntent = Intent().apply {
        setClassName(context.packageName, "com.gasguru.MainActivity")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra("station_id", stationItem.idServiceStation.toString())
    }
    val verticalPadding = if (isCompact) 4.dp else 8.dp
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp, vertical = verticalPadding)
            .clickable(actionStartActivity(openDetailIntent)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = stationItem.name,
                style = WidgetStyleBodyMedium.copy(color = GlanceTheme.colors.onSurface),
                maxLines = 1,
            )
            Text(
                text = stationItem.direction,
                style = WidgetStyleCaption.copy(color = GlanceTheme.colors.onSurfaceVariant),
                maxLines = 1,
            )
        }
        PriceChip(
            formattedPrice = stationItem.formattedPrice,
            priceCategory = stationItem.priceCategory,
        )
    }
}

@Composable
private fun PriceChip(formattedPrice: String, priceCategory: PriceCategory) {
    val textColor = when (priceCategory) {
        PriceCategory.CHEAP -> WidgetColors.accentGreen
        PriceCategory.NORMAL -> WidgetColors.accentOrange
        PriceCategory.EXPENSIVE -> WidgetColors.accentRed
        PriceCategory.NONE -> GlanceTheme.colors.onSurface
    }
    val bgColor = when (priceCategory) {
        PriceCategory.CHEAP -> WidgetColors.accentGreenAlpha
        PriceCategory.NORMAL -> WidgetColors.accentOrangeAlpha
        PriceCategory.EXPENSIVE -> WidgetColors.accentRedAlpha
        PriceCategory.NONE -> GlanceTheme.colors.surfaceVariant
    }
    Box(
        modifier = GlanceModifier
            .background(bgColor)
            .cornerRadius(16.dp)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = formattedPrice,
            style = WidgetStylePrice.copy(color = textColor),
        )
    }
}
