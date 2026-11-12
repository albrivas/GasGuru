package com.gasguru.core.uikit.components.swipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.MyApplicationTheme
import kotlin.test.Test
import kotlin.test.assertEquals

private const val ROW_TAG = "swipe_row"
private const val ACTION_TAG = "swipe_action_delete"

@OptIn(ExperimentalTestApi::class)
class SwipeItemTest {

    private var actionClicks = 0
    private var rowClicks = 0
    private val revealedChanges = mutableListOf<Boolean>()

    private fun swipeModel(isRevealed: Boolean = false) = SwipeItemModel(
        icon = Icons.Default.Delete,
        backgroundColor = Color.Red,
        onClick = { actionClicks++ },
        isRevealed = isRevealed,
        onRevealedChange = { revealed -> revealedChanges.add(revealed) },
    )

    @Composable
    private fun SwipeRow(model: SwipeItemModel) {
        MyApplicationTheme {
            SwipeItem(model = model) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag(ROW_TAG)
                        .clickable { rowClicks++ },
                ) {
                    Text(text = "Station")
                }
            }
        }
    }

    @Test
    fun `swipe left reveals the action without deleting`() = runComposeUiTest {
        setContent { SwipeRow(model = swipeModel()) }

        onNodeWithTag(ROW_TAG).performTouchInput { swipeLeft() }
        waitForIdle()

        onNodeWithTag(ROW_TAG).assertIsDisplayed()
        onNodeWithTag(ACTION_TAG).assertIsDisplayed()
        assertEquals(0, actionClicks)
        assertEquals(listOf(true), revealedChanges)
    }

    @Test
    fun `tapping the revealed action triggers the delete`() = runComposeUiTest {
        setContent { SwipeRow(model = swipeModel()) }

        onNodeWithTag(ROW_TAG).performTouchInput { swipeLeft() }
        onNodeWithTag(ACTION_TAG).performClick()

        assertEquals(1, actionClicks)
    }

    @Test
    fun `swipe right does not reveal the action`() = runComposeUiTest {
        setContent { SwipeRow(model = swipeModel()) }

        onNodeWithTag(ROW_TAG).performTouchInput { swipeRight() }
        waitForIdle()

        onNodeWithTag(ACTION_TAG).assertDoesNotExist()
        assertEquals(0, actionClicks)
    }

    @Test
    fun `tapping the row while revealed closes it without opening the row`() = runComposeUiTest {
        setContent { SwipeRow(model = swipeModel()) }

        onNodeWithTag(ROW_TAG).performTouchInput { swipeLeft() }
        onNodeWithTag(ROW_TAG).performClick()
        waitForIdle()

        onNodeWithTag(ACTION_TAG).assertDoesNotExist()
        assertEquals(0, rowClicks)
        assertEquals(listOf(true, false), revealedChanges)
    }

    @Test
    fun `tapping the row while closed keeps its own click`() = runComposeUiTest {
        setContent { SwipeRow(model = swipeModel()) }

        onNodeWithTag(ROW_TAG).performClick()

        assertEquals(1, rowClicks)
    }

    @Test
    fun `parent closing the item hides the action`() = runComposeUiTest {
        var isRevealed by mutableStateOf(true)
        setContent { SwipeRow(model = swipeModel(isRevealed = isRevealed)) }
        onNodeWithTag(ACTION_TAG).assertIsDisplayed()

        isRevealed = false
        waitForIdle()

        onNodeWithTag(ACTION_TAG).assertDoesNotExist()
    }

    @Test
    fun `accessibility custom action deletes without swiping`() = runComposeUiTest {
        setContent { SwipeRow(model = swipeModel()) }

        val deleteAction = onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions))
            .fetchSemanticsNode()
            .config[SemanticsActions.CustomActions]
            .single()
        runOnIdle { deleteAction.action() }

        assertEquals(1, actionClicks)
    }
}
