package com.albrivas.fuelpump.navigation.navigationbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.albrivas.fuelpump.R
import com.albrivas.fuelpump.core.uikit.theme.GreenDark
import com.albrivas.fuelpump.navigation.navigationbar.route.TopLevelRoutes

@Composable
internal fun NavigationBottomBar(navController: NavHostController) {
    val state = rememberNavigationBarState(navController)
    NavigationBar(
        containerColor = Color.White,
    ) {
        state.topLevelRoutes.forEach { destination ->
            when (destination) {
                is TopLevelRoutes.Favorite ->
                    BarItem(
                        icon = destination.icon,
                        selectedIcon = destination.selectedIcon,
                        label = stringResource(id = R.string.list_nav),
                        isSelected = destination.route == TopLevelRoutes.fromRoute(state.currentDestination?.route),
                        onNavigateToDestination = { state.onNavItemClick(it) },
                        destination = destination
                    )

                is TopLevelRoutes.Map ->
                    BarItem(
                        icon = destination.icon,
                        selectedIcon = destination.selectedIcon,
                        label = stringResource(id = R.string.map_nav),
                        isSelected = destination.route == TopLevelRoutes.fromRoute(state.currentDestination?.route),
                        onNavigateToDestination = { state.onNavItemClick(it) },
                        destination = destination
                    )

                is TopLevelRoutes.Profile ->
                    BarItem(
                        icon = destination.icon,
                        selectedIcon = destination.selectedIcon,
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
    icon: Int,
    selectedIcon: Int,
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
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        },
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(id = if (isSelected) selectedIcon else icon),
                contentDescription = null
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = GreenDark,
            selectedTextColor = GreenDark,
            indicatorColor = Color.White,
            unselectedIconColor = Color.Black
        ),
        onClick = {
            onNavigateToDestination(destination)
        }
    )
}

@Preview
@Composable
private fun NavigationBarPreview() {
    NavigationBottomBar(
        navController = rememberNavController()
    )
}
