package com.gasguru.navigation.navigationbar.route

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.gasguru.composeApp.generated.resources.Res
import com.gasguru.composeApp.generated.resources.list_nav
import com.gasguru.composeApp.generated.resources.map_nav
import com.gasguru.composeApp.generated.resources.profile_nav
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed class TopLevelRoutes {
    abstract val icon: ImageVector
    abstract val label: StringResource

    @Serializable
    data object Map : TopLevelRoutes() {
        override val icon: ImageVector get() = Icons.Outlined.LocationOn
        override val label: StringResource get() = Res.string.map_nav
    }

    @Serializable
    data object Favorite : TopLevelRoutes() {
        override val icon: ImageVector get() = Icons.Outlined.FavoriteBorder
        override val label: StringResource get() = Res.string.list_nav
    }

    @Serializable
    data object Profile : TopLevelRoutes() {
        override val icon: ImageVector get() = Icons.Outlined.AccountCircle
        override val label: StringResource get() = Res.string.profile_nav
    }
}
