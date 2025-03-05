package com.gasguru.core.uikit.fuel_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.components.selectedItem.BasicSelectedItem
import com.gasguru.core.uikit.components.selectedItem.BasicSelectedItemModel
import com.gasguru.core.uikit.theme.MyApplicationTheme

@Composable
fun FuelListSelection(model: FuelListSelectionModel, modifier: Modifier = Modifier) = with(model) {
    var selectedFuel by remember { mutableStateOf(selected) }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(list) { index, fuel ->
            val image = fuel.first
            val name = fuel.second
            BasicSelectedItem(
                modifier = Modifier.testTag("list_item_$index"),
                model = BasicSelectedItemModel(
                    title = name,
                    isSelected = name == selectedFuel,
                    image = image,
                    onItemSelected = {
                        selectedFuel = name
                        onItemSelected(name)
                    }
                ),
            )
        }
    }
}

@Preview
@Composable
private fun FuelListSelectionPreview() {
    MyApplicationTheme {
        FuelListSelection(
            model = FuelListSelectionModel(
                list = listOf(
                    Pair(
                        R.drawable.ic_gasoline_95,
                        R.string.preview_fuel_type
                    )
                ),
                selected = R.string.preview_fuel_type,
                onItemSelected = {
                }
            )
        )
    }
}
