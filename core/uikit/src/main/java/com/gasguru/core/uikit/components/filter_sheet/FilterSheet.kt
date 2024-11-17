package com.gasguru.core.uikit.components.filter_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.Neutral500
import com.gasguru.core.uikit.theme.Primary600
import com.gasguru.core.uikit.theme.TextMain
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(model: FilterSheetModel, modifier: Modifier = Modifier) {
    val state = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = { model.onDismiss() },
        sheetState = state,
        containerColor = Color.White,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.large,
        contentWindowInsets = { WindowInsets.navigationBars },
        dragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray,
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
        modifier = modifier.padding(top = 140.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheetContent(model: FilterSheetModel, onDismiss: () -> Unit) = with(model) {
    val listSelection = remember {
        mutableStateListOf<String>().apply { addAll(optionsSelected) }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
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
                color = TextMain,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close filter",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onDismiss() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            options.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .padding(horizontal = 8.dp)
                        .clickable { handleSelectionItem(item, listSelection, isMultiOption) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        text = item,
                    )

                    RadioButton(
                        selected = listSelection.contains(item),
                        onClick = { handleSelectionItem(item, listSelection, isMultiOption) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Primary600,
                            unselectedColor = Neutral500
                        ),
                    )
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
}

fun handleSelectionItem(
    item: String,
    listSelection: MutableList<String>,
    isMultiOption: Boolean,
) {
    if (listSelection.contains(item)) {
        listSelection.remove(item)
    } else {
        if (isMultiOption) {
            listSelection.add(item)
        } else {
            listSelection.clear()
            listSelection.add(item)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterSheetContentPreview() {
    FilterSheetContent(
        model = FilterSheetModel(
            title = "Estaciones de servicio",
            buttonText = "Save",
            isMultiOption = true,
            options = listOf("Repsol", "Cepsa", "BP"),
            optionsSelected = listOf("Repsol"),
            onSaveButton = {},
            onDismiss = {},
        ),
        onDismiss = {}
    )
}
