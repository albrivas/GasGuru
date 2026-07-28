package com.gasguru.core.uikit.components.filter_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.bottom_sheet.GasGuruBottomSheet
import com.gasguru.core.uikit.components.bottom_sheet.GasGuruBottomSheetAction
import com.gasguru.core.uikit.components.bottom_sheet.GasGuruBottomSheetModel
import com.gasguru.core.uikit.components.icon.CircleIcon
import com.gasguru.core.uikit.components.icon.CircleIconModel
import com.gasguru.core.uikit.components.icon.FuelStationIcons
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.core.uikit.utils.horizontalDivider

@Composable
fun FilterSheet(model: FilterSheetModel, modifier: Modifier = Modifier) = with(model) {
    val listSelection = remember {
        mutableStateListOf<String>().apply { addAll(optionsSelected) }
    }

    GasGuruBottomSheet(
        model = GasGuruBottomSheetModel(
            title = title,
            onDismiss = onDismiss,
            actionButton = GasGuruBottomSheetAction(
                text = buttonText,
                onClick = { onSaveButton(listSelection) },
            ),
        ),
        modifier = modifier,
    ) {
        FilterSheetOptions(model = model, listSelection = listSelection)
    }
}

@Composable
private fun FilterSheetOptions(model: FilterSheetModel, listSelection: MutableList<String>) = with(model) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(8.dp))
            .background(GasGuruTheme.colors.neutralWhite)
    ) {
        val neutral300 = GasGuruTheme.colors.neutral300
        options.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(color = GasGuruTheme.colors.neutralWhite)
                    .padding(start = 8.dp, end = 8.dp)
                    .clickable {
                        handleSelectionItem(
                            item,
                            listSelection,
                            isMultiOption,
                            isMustSelection
                        )
                    }
                    .horizontalDivider(color = neutral300, isLastItem = index == options.size - 1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                iconMap?.get(item)?.let { iconResId ->
                    CircleIcon(model = CircleIconModel(icon = iconResId, size = 32.dp))
                }

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    style = if (listSelection.contains(
                            item
                        )
                    ) {
                        GasGuruTheme.typography.baseBold
                    } else {
                        GasGuruTheme.typography.baseRegular
                    },
                    color = GasGuruTheme.colors.neutralBlack,
                    textAlign = TextAlign.Start,
                    text = item,
                )

                RadioButton(
                    selected = listSelection.contains(item),
                    onClick = {
                        handleSelectionItem(
                            item = item,
                            listSelection = listSelection,
                            isMultiOption = isMultiOption,
                            isMustSelection = isMustSelection
                        )
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = GasGuruTheme.colors.primary600,
                        unselectedColor = GasGuruTheme.colors.neutral500
                    ),
                )
            }
        }
    }
}

fun handleSelectionItem(
    item: String,
    listSelection: MutableList<String>,
    isMultiOption: Boolean,
    isMustSelection: Boolean,
) {
    if (listSelection.contains(item)) {
        if (!isMustSelection) {
            listSelection.remove(item)
        }
    } else {
        if (isMultiOption) {
            listSelection.add(item)
        } else {
            listSelection.clear()
            listSelection.add(item)
        }
    }
}

@Composable
@ThemePreviews
private fun FilterSheetOptionsPreview() {
    val model = FilterSheetModel(
        title = "Estaciones de servicio",
        buttonText = "Save",
        isMultiOption = true,
        isMustSelection = false,
        options = listOf("Repsol", "Cepsa", "BP"),
        optionsSelected = listOf("Repsol"),
        onSaveButton = {},
        onDismiss = {},
        iconMap = mapOf(
            "Repsol" to FuelStationIcons.Repsol,
            "Cepsa" to FuelStationIcons.Cepsa,
            "BP" to FuelStationIcons.Bp
        )
    )
    FilterSheetOptions(
        model = model,
        listSelection = remember { mutableStateListOf<String>().apply { addAll(model.optionsSelected) } },
    )
}
