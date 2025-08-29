package com.gasguru.navigation.navigationbar.route

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.gasguru.R
import kotlinx.serialization.Serializable

@Serializable
sealed class TopLevelRoutes {
    abstract val icon: ImageVector
    abstract val labelRes: Int

    @Serializable
    data object Map : TopLevelRoutes() {
        override val icon = Icons.Outlined.LocationOn
        override val labelRes = R.string.map_nav
    }

    @Serializable
    data object Favorite : TopLevelRoutes() {
        override val icon = Icons.Outlined.FavoriteBorder
        override val labelRes = R.string.list_nav
    }

    @Serializable
    data object Profile : TopLevelRoutes() {
        override val icon = Icons.Outlined.AccountCircle
        override val labelRes = R.string.profile_nav
    }
}
