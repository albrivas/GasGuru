package com.albrivas.fuelpump.feature.home.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.toRoute
import com.albrivas.fuelpump.core.uikit.theme.GreenDark
import com.albrivas.fuelpump.feature.home.navigation.route.HomeTopLevelRoutes

@Composable
internal fun NavigationBottomBar(model: NavigationBarModel) {
    NavigationBar(
        containerColor = Color.White,
    ) {
        with(model) {
            destinations.forEach { destination ->
                when (destination) {
                    is HomeTopLevelRoutes.List -> BarItem(
                        destination.icon,
                        destination.route == currentDestination?.toRoute<HomeTopLevelRoutes.List>()?.route,
                        onNavigateToDestination,
                        destination
                    )

                    is HomeTopLevelRoutes.Map -> BarItem(
                        destination.icon,
                        destination.route == currentDestination?.toRoute<HomeTopLevelRoutes.Map>()?.route,
                        onNavigateToDestination,
                        destination
                    )

                    is HomeTopLevelRoutes.Profile -> BarItem(
                        destination.icon,
                        destination.route == currentDestination?.toRoute<HomeTopLevelRoutes.Profile>()?.route,
                        onNavigateToDestination,
                        destination
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.BarItem(
    icon: Int,
    isSelected: Boolean,
    onNavigateToDestination: (HomeTopLevelRoutes) -> Unit,
    destination: HomeTopLevelRoutes
) {
    NavigationBarItem(
        selected = isSelected,
        icon = {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = ImageVector.vectorResource(id = icon),
                contentDescription = null
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = GreenDark,
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
        model = NavigationBarModel(
            destinations = listOf(
                HomeTopLevelRoutes.Map(),
                HomeTopLevelRoutes.List(),
                HomeTopLevelRoutes.Profile()
            ),
            currentDestination = null,
            onNavigateToDestination = {}
        )
    )
}