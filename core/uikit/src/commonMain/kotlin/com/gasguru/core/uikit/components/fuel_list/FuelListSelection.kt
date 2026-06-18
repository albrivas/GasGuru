package com.gasguru.core.uikit.components.fuel_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.selectedItem.SelectedItem
import com.gasguru.core.uikit.components.selectedItem.SelectedItemModel
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_gasoline_95
import com.gasguru.core.uikit.generated.resources.preview_fuel_type
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.core.uikit.utils.maestroTestTag
import org.jetbrains.compose.resources.stringResource

@Composable
fun FuelListSelection(
    model: FuelListSelectionModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) = with(model) {
    var selectedFuel by remember { mutableStateOf(selected) }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(list) { index, fuel ->
            SelectedItem(
                modifier = Modifier.maestroTestTag("list_item_$index"),
                model = SelectedItemModel(
                    title = fuel.nameRes,
                    isSelected = fuel.nameRes == selectedFuel,
                    image = fuel.iconRes,
                    onItemSelected = {
                        selectedFuel = fuel.nameRes
                        onItemSelected(fuel.nameRes)
                    },
                ),
            )
        }
    }
}

@Composable
@ThemePreviews
private fun FuelListSelectionPreview() {
    MyApplicationTheme {
        FuelListSelection(
            model = FuelListSelectionModel(
                list = listOf(
                    FuelItemModel(
                        iconRes = Res.drawable.ic_gasoline_95,
                        nameRes = stringResource(Res.string.preview_fuel_type),
                    ),
                ),
                selected = stringResource(Res.string.preview_fuel_type),
                onItemSelected = {},
            ),
        )
    }
}
