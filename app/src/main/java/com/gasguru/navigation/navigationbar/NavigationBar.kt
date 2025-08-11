package com.gasguru.navigation.navigationbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
internal fun NavigationBottomBar(navController: NavHostController) {
    val state = rememberNavigationBarState(navController)
    NavigationBar(
        containerColor = GasGuruTheme.colors.neutralWhite,
    ) {
        state.topLevelRoutes.forEach { destination ->
            val isSelected = state.isSelected(destination)
            BarItem(
                icon = destination.icon,
                label = stringResource(id = destination.labelRes),
                isSelected = isSelected,
                onNavigateToDestination = { 
                    if (!isSelected) {
                        state.onNavItemClick(destination)
                    }
                },
            )
        }
    }
}

@Composable
private fun RowScope.BarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onNavigateToDestination: () -> Unit,
) {
    NavigationBarItem(
        selected = isSelected,
        label = {
            Text(
                text = label,
                style = if (isSelected) {
                    GasGuruTheme.typography.captionBold
                } else {
                    GasGuruTheme.typography.captionRegular
                },
            )
        },
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                contentDescription = null,
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = GasGuruTheme.colors.primary600,
            selectedTextColor = GasGuruTheme.colors.primary600,
            indicatorColor = GasGuruTheme.colors.primary600.copy(alpha = 0.16f),
            unselectedIconColor = GasGuruTheme.colors.neutral600,
            unselectedTextColor = GasGuruTheme.colors.textSubtle
        ),
        onClick = onNavigateToDestination
    )
}

@Composable
@ThemePreviews
private fun NavigationBarPreview() {
    NavigationBottomBar(
        navController = rememberNavController()
    )
}
