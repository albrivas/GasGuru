package com.gasguru.navigation.navigationbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
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
import com.gasguru.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.navigation.navigationbar.route.TopLevelRoutes

@Composable
internal fun NavigationBottomBar(navController: NavHostController) {
    val state = rememberNavigationBarState(navController)
    NavigationBar(
        containerColor = GasGuruTheme.colors.neutralWhite,
    ) {
        state.topLevelRoutes.forEach { destination ->
            when (destination) {
                is TopLevelRoutes.Favorite ->
                    BarItem(
                        icon = Icons.Outlined.FavoriteBorder,
                        label = stringResource(id = R.string.list_nav),
                        isSelected = destination.route == TopLevelRoutes.fromRoute(state.currentDestination?.route),
                        onNavigateToDestination = { state.onNavItemClick(it) },
                        destination = destination
                    )

                is TopLevelRoutes.Map ->
                    BarItem(
                        icon = Icons.Outlined.LocationOn,
                        label = stringResource(id = R.string.map_nav),
                        isSelected = destination.route == TopLevelRoutes.fromRoute(state.currentDestination?.route),
                        onNavigateToDestination = { state.onNavItemClick(it) },
                        destination = destination
                    )

                is TopLevelRoutes.Profile ->
                    BarItem(
                        icon = Icons.Outlined.AccountCircle,
                        label = stringResource(id = R.string.profile_nav),
                        isSelected = destination.route == TopLevelRoutes.fromRoute(state.currentDestination?.route),
                        onNavigateToDestination = { state.onNavItemClick(it) },
                        destination = destination
                    )
            }
        }
    }
}

@Composable
private fun RowScope.BarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onNavigateToDestination: (TopLevelRoutes) -> Unit,
    destination: TopLevelRoutes,
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
        onClick = {
            onNavigateToDestination(destination)
        }
    )
}

@Composable
@ThemePreviews
private fun NavigationBarPreview() {
    NavigationBottomBar(
        navController = rememberNavController()
    )
}
