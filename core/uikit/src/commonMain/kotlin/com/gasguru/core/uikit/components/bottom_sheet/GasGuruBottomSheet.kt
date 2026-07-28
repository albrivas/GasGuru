package com.gasguru.core.uikit.components.bottom_sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.drag_handle.DragHandle
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.bottom_sheet_close_content_description
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.utils.TestTags
import com.gasguru.core.uikit.utils.maestroTestTag
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasGuruBottomSheet(
    model: GasGuruBottomSheetModel,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = model.skipPartiallyExpanded)
    val coroutineScope = rememberCoroutineScope()

    fun animatedDismiss(onHidden: () -> Unit = {}) {
        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onHidden()
                model.onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = model.onDismiss,
        sheetState = sheetState,
        containerColor = GasGuruTheme.colors.neutral100,
        contentColor = GasGuruTheme.colors.neutral100,
        shape = MaterialTheme.shapes.large,
        contentWindowInsets = { WindowInsets.navigationBars },
        dragHandle = { DragHandle() },
        modifier = modifier.statusBarsPadding().maestroTestTag(TestTags.BottomSheet.SHEET),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = model.title,
                    style = GasGuruTheme.typography.baseBold,
                    color = GasGuruTheme.colors.textMain,
                )
                model.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = GasGuruTheme.typography.captionRegular,
                        color = GasGuruTheme.colors.neutral600,
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.bottom_sheet_close_content_description),
                tint = GasGuruTheme.colors.neutralBlack,
                modifier = Modifier
                    .size(size = 32.dp)
                    .maestroTestTag(TestTags.BottomSheet.CLOSE)
                    .clickable { animatedDismiss() },
            )
        }

        Column(modifier = Modifier.weight(weight = 1f, fill = false)) {
            content { animatedDismiss() }
        }

        model.actionButton?.let { action ->
            GasGuruButton(
                onClick = { animatedDismiss(onHidden = action.onClick) },
                enabled = action.enabled,
                text = action.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}
