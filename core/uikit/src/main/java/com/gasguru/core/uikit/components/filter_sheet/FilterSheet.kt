package com.gasguru.core.uikit.components.filter_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.icon.CircleIcon
import com.gasguru.core.uikit.components.icon.CircleIconModel
import com.gasguru.core.uikit.components.icon.FuelStationIcons
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(model: FilterSheetModel, modifier: Modifier = Modifier) {
    val state = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = { model.onDismiss() },
        sheetState = state,
        containerColor = GasGuruTheme.colors.neutral100,
        contentColor = GasGuruTheme.colors.neutral100,
        shape = MaterialTheme.shapes.large,
        contentWindowInsets = { WindowInsets.navigationBars },
        dragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                color = GasGuruTheme.colors.neutral700,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(
                    modifier = Modifier.size(
                        width = 32.dp,
                        height = 4.0.dp
                    )
                )
            }
        },
        modifier = modifier.statusBarsPadding()
    ) {
        FilterSheetContent(model = model, onDismiss = {
            coroutineScope.launch { state.hide() }.invokeOnCompletion {
                if (!state.isVisible) {
                    model.onDismiss()
                }
            }
        })
    }
}

@Composable
private fun FilterSheetContent(model: FilterSheetModel, onDismiss: () -> Unit) = with(model) {
    val listSelection = remember {
        mutableStateListOf<String>().apply { addAll(optionsSelected) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = GasGuruTheme.typography.baseBold,
                color = GasGuruTheme.colors.textMain,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close filter",
                tint = GasGuruTheme.colors.neutralBlack,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onDismiss() }
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f, fill = false)
                .padding(start = 16.dp, end = 16.dp)
                .background(GasGuruTheme.colors.neutralWhite)
                .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(8.dp))
        ) {
            val neutral300 = GasGuruTheme.colors.neutral300
            options.forEach { item ->
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
                        }.drawBehind {
                            if (item != options.last()) {
                                val lineY = size.height - 1.dp.toPx()
                                drawLine(
                                    color = neutral300,
                                    start = Offset(0f, lineY),
                                    end = Offset(size.width, lineY),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                        },
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

        GasGuruButton(
            onClick = {
                onSaveButton(listSelection)
                onDismiss()
            },
            enabled = true,
            text = buttonText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
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
private fun FilterSheetContentPreview() {
    FilterSheetContent(
        model = FilterSheetModel(
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
        ),
        onDismiss = {}
    )
}
