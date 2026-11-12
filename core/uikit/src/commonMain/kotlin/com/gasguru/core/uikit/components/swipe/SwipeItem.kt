package com.gasguru.core.uikit.components.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.animation.GasGuruLottie
import com.gasguru.core.uikit.components.fuelItem.FuelStationItem
import com.gasguru.core.uikit.components.fuelItem.FuelStationItemModel
import com.gasguru.core.uikit.components.icon.FuelStationIcons
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.swipe_action_delete
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.core.uikit.utils.maestroTestTag
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private val ACTION_WIDTH = 80.dp
private val ACTION_ICON_SIZE = 24.dp

private enum class SwipeRevealValue { Closed, Revealed }

// Swiping end-to-start stops at the action instead of firing it: the user must tap to confirm.
@Composable
fun SwipeItem(
    model: SwipeItemModel,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val actionWidthPx = with(LocalDensity.current) { ACTION_WIDTH.toPx() }
    val swipeState = remember {
        AnchoredDraggableState(
            initialValue = if (model.isRevealed) SwipeRevealValue.Revealed else SwipeRevealValue.Closed,
        )
    }
    SideEffect {
        swipeState.updateAnchors(
            newAnchors = DraggableAnchors {
                SwipeRevealValue.Closed at 0f
                SwipeRevealValue.Revealed at -actionWidthPx
            },
        )
    }

    val currentOnRevealedChange by rememberUpdatedState(model.onRevealedChange)
    LaunchedEffect(swipeState) {
        snapshotFlow { swipeState.settledValue }
            .drop(count = 1)
            .collect { settledValue -> currentOnRevealedChange(settledValue == SwipeRevealValue.Revealed) }
    }
    LaunchedEffect(model.isRevealed) {
        if (!model.isRevealed && swipeState.targetValue == SwipeRevealValue.Revealed) {
            swipeState.animateTo(targetValue = SwipeRevealValue.Closed)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val actionLabel = stringResource(resource = Res.string.swipe_action_delete)
    val swipeOffset = swipeState.offset.takeUnless { offset -> offset.isNaN() } ?: 0f
    val isRevealed = swipeState.settledValue == SwipeRevealValue.Revealed

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        if (swipeOffset < 0f) {
            SwipeAction(
                model = model,
                contentDescription = actionLabel,
                isRevealed = isRevealed,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(x = swipeOffset.roundToInt(), y = 0) }
                .anchoredDraggable(state = swipeState, orientation = Orientation.Horizontal)
                .background(color = GasGuruTheme.colors.neutralWhite)
                .semantics {
                    customActions = listOf(
                        CustomAccessibilityAction(label = actionLabel) {
                            model.onClick()
                            true
                        },
                    )
                },
        ) {
            content()
            if (isRevealed) {
                // While open, a tap on the row closes it instead of triggering the row's own click.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                coroutineScope.launch {
                                    swipeState.animateTo(targetValue = SwipeRevealValue.Closed)
                                }
                            },
                        ),
                )
            }
        }
    }
}

@Composable
private fun SwipeAction(
    model: SwipeItemModel,
    contentDescription: String,
    isRevealed: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(ACTION_WIDTH)
            .fillMaxHeight()
            .background(color = model.backgroundColor)
            .clickable(onClick = model.onClick)
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            }
            .maestroTestTag("swipe_action_delete"),
        contentAlignment = Alignment.Center,
    ) {
        if (model.iconAnimationFile != null) {
            GasGuruLottie(
                filePath = model.iconAnimationFile,
                modifier = Modifier.size(ACTION_ICON_SIZE),
                isPlaying = isRevealed,
            )
        } else {
            Icon(
                imageVector = model.icon,
                contentDescription = null,
                modifier = Modifier.size(ACTION_ICON_SIZE),
                tint = GasGuruTheme.colors.neutralWhite,
            )
        }
    }
}

@Composable
@ThemePreviews
private fun SwipeItemRevealedPreview() {
    MyApplicationTheme {
        SwipeItem(
            model = SwipeItemModel(
                icon = Icons.Default.Delete,
                backgroundColor = GasGuruTheme.colors.red500,
                onClick = {},
                isRevealed = true,
            ),
        ) {
            FuelStationItem(
                model = FuelStationItemModel(
                    idServiceStation = 1,
                    icon = FuelStationIcons.Repsol,
                    name = "EDAN REPSOL",
                    distance = "567 m",
                    price = "1.75 €/l",
                    index = 3686,
                    categoryColor = GasGuruTheme.colors.red500,
                    onItemClick = {},
                ),
                isLastItem = false,
            )
        }
    }
}
