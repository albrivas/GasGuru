package com.albrivas.fuelpump.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.UserData
import com.albrivas.fuelpump.core.ui.translation
import com.albrivas.fuelpump.core.uikit.components.FuelPumpButton
import com.albrivas.fuelpump.core.uikit.components.selectedItem.BasicSelectedItem
import com.albrivas.fuelpump.core.uikit.components.selectedItem.BasicSelectedItemModel
import com.albrivas.fuelpump.core.uikit.components.settings.SettingItem
import com.albrivas.fuelpump.core.uikit.components.settings.SettingItemModel
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
internal fun ProfileScreenRoute(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.userData.collectAsStateWithLifecycle()
    return ProfileScreen(uiState = state, saveFuelType = viewModel::saveSelectionFuel)
}

@Composable
internal fun ProfileScreen(uiState: ProfileUiState, saveFuelType: (FuelType) -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var fuelSelected by remember { mutableStateOf(FuelType.GASOLINE_95) }

    when (uiState) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding()
                    .testTag("loading"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is ProfileUiState.Success -> {
            Column(
                modifier = Modifier
                    .background(color = Color.White)
                    .fillMaxSize()
            ) {
                fuelSelected = uiState.userData.fuelSelection
                SettingItem(
                    model = SettingItemModel(
                        title = stringResource(id = R.string.fuel_selection),
                        selection = stringResource(id = uiState.userData.fuelSelection.translation()),
                        icon = R.drawable.ic_fuel_station,
                        onClick = { showDialog = true },
                    ),
                    modifier = Modifier.testTag("fuel_setting_item")
                )
            }
        }

        is ProfileUiState.LoadFailed -> {
            // Error state
        }
    }

    if (showDialog) {
        SelectionDialog(
            fuelSelected = fuelSelected,
            onDismiss = { showDialog = false },
            saveFuelType = saveFuelType
        )
    }
}

@Composable
fun SelectionDialog(
    fuelSelected: FuelType,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    saveFuelType: (FuelType) -> Unit = {},
) {
    var itemSelected by remember { mutableStateOf(fuelSelected) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp)
                .testTag("fuel_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Scaffold(
                modifier = modifier
                    .wrapContentSize()
                    .background(color = Color.White),
                containerColor = Color.White,
                topBar = {
                    Box(
                        modifier = Modifier.padding(
                            start = 24.dp,
                            bottom = 16.dp,
                            end = 24.dp,
                            top = 24.dp
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.fuel),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                bottomBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FuelPumpButton(
                            onClick = {
                                saveFuelType(itemSelected)
                                onDismiss()
                            },
                            text = "OK",
                            modifier = Modifier
                                .width(80.dp)
                        )
                    }
                },
                content = { innerPadding ->
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(Color.White)
                            .padding(innerPadding)
                    ) {
                        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp)
                                .background(Color.White)
                        ) {
                            items(FuelType.entries) { fuel ->
                                BasicSelectedItem(
                                    model = BasicSelectedItemModel(
                                        title = fuel.translation(),
                                        isSelected = fuel == itemSelected,
                                        onItemSelected = {
                                            itemSelected = fuel
                                        },
                                        isRoundedItem = false
                                    ),
                                )
                            }
                        }
                        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun DialogPreview() {
    MyApplicationTheme {
        SelectionDialog(fuelSelected = FuelType.GASOLINE_95)
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    MyApplicationTheme {
        ProfileScreen(uiState = ProfileUiState.Success(userData = UserData(fuelSelection = FuelType.GASOLINE_95)))
    }
}

@Preview
@Composable
private fun ProfileScreenLoadingPreview() {
    MyApplicationTheme {
        ProfileScreen(uiState = ProfileUiState.Loading)
    }
}
