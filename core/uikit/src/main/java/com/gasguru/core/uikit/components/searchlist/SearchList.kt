package com.gasguru.core.uikit.components.searchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.components.placeitem.PlaceItem
import com.gasguru.core.uikit.components.placeitem.PlaceItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun SearchList(
    model: SearchListModel,
    modifier: Modifier = Modifier
) {
    if (model.items.isEmpty()) {
        EmptySearchList(
            type = model.type,
            modifier = modifier
        )
    } else {
        SearchListContent(
            model = model,
            modifier = modifier
        )
    }
}

@Composable
private fun SearchListContent(
    model: SearchListModel,
    modifier: Modifier = Modifier
) {
    val title = when (model.type) {
        SearchListType.RECENT -> stringResource(R.string.search_list_recent_title)
        SearchListType.SUGGESTIONS -> stringResource(R.string.search_list_suggestions_title)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutral100)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = GasGuruTheme.typography.baseBold,
                color = GasGuruTheme.colors.textMain
            )
            
            if (model.type == SearchListType.RECENT && model.onClear != null) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.search_list_clear_description),
                    tint = GasGuruTheme.colors.neutralBlack,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .clickable { model.onClear.invoke() }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(model.items) { item ->
                PlaceItem(
                    model = item,
                    isLastItem = false
                )
            }
        }
    }
}

@Composable
private fun EmptySearchList(
    type: SearchListType,
    modifier: Modifier = Modifier
) {
    val title = when (type) {
        SearchListType.RECENT -> stringResource(R.string.search_list_recent_title)
        SearchListType.SUGGESTIONS -> stringResource(R.string.search_list_suggestions_title)
    }
    
    val emptyMessage = when (type) {
        SearchListType.RECENT -> stringResource(R.string.search_list_empty_recent)
        SearchListType.SUGGESTIONS -> stringResource(R.string.search_list_empty_suggestions)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutral100)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp),
            style = GasGuruTheme.typography.h6,
            color = GasGuruTheme.colors.textMain
        )
        Text(
            text = emptyMessage,
            modifier = Modifier.align(Alignment.Start),
            style = GasGuruTheme.typography.baseRegular,
            color = GasGuruTheme.colors.textSubtle
        )
    }
}


@Composable
@ThemePreviews
private fun SearchListRecentPreview() {
    MyApplicationTheme {
        SearchList(
            model = SearchListModel(
                type = SearchListType.RECENT,
                items = listOf(
                    PlaceItemModel("1", Icons.Outlined.AccessTime, "Barcelona") {},
                    PlaceItemModel("2", Icons.Outlined.AccessTime, "Madrid") {},
                    PlaceItemModel("3", Icons.Outlined.AccessTime, "Valencia") {},
                ),
                onClear = {}
            )
        )
    }
}

@Composable
@ThemePreviews
private fun SearchListSuggestionsPreview() {
    MyApplicationTheme {
        SearchList(
            model = SearchListModel(
                type = SearchListType.SUGGESTIONS,
                items = listOf(
                    PlaceItemModel("1", Icons.Outlined.LocationOn, "Barcelona, España") {},
                    PlaceItemModel("2", Icons.Outlined.LocationOn, "Madrid, España") {},
                    PlaceItemModel("3", Icons.Outlined.LocationOn, "Valencia, España") {},
                )
            )
        )
    }
}

@Composable
@ThemePreviews
private fun EmptySearchListRecentPreview() {
    MyApplicationTheme {
        SearchList(
            model = SearchListModel(
                type = SearchListType.RECENT,
                items = emptyList(),
                onClear = {}
            )
        )
    }
}

@Composable
@ThemePreviews
private fun EmptySearchListSuggestionsPreview() {
    MyApplicationTheme {
        SearchList(
            model = SearchListModel(
                type = SearchListType.SUGGESTIONS,
                items = emptyList()
            )
        )
    }
}