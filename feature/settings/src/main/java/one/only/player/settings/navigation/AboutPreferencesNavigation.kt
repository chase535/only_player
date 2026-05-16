package one.only.player.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import one.only.player.settings.screens.about.AboutPreferencesScreen
import one.only.player.settings.screens.about.LibrariesScreen
import one.only.player.settings.screens.about.LogsScreen

const val aboutPreferencesNavigationRoute = "about_preferences_route"
const val librariesNavigationRoute = "libraries_route"
const val logsNavigationRoute = "logs_route"

fun NavController.navigateToAboutPreferences(navOptions: NavOptions? = navOptions { launchSingleTop = true }) {
    this.navigate(aboutPreferencesNavigationRoute, navOptions)
}

fun NavController.navigateToLibraries(navOptions: NavOptions? = navOptions { launchSingleTop = true }) {
    this.navigate(librariesNavigationRoute, navOptions)
}

fun NavController.navigateToLogs(navOptions: NavOptions? = navOptions { launchSingleTop = true }) {
    this.navigate(logsNavigationRoute, navOptions)
}

fun NavGraphBuilder.aboutPreferencesScreen(
    onLibrariesClick: () -> Unit,
    onLogsClick: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable(route = aboutPreferencesNavigationRoute) {
        AboutPreferencesScreen(
            onLibrariesClick = onLibrariesClick,
            onLogsClick = onLogsClick,
            onNavigateUp = onNavigateUp,
        )
    }
}

fun NavGraphBuilder.librariesScreen(
    onNavigateUp: () -> Unit,
) {
    composable(route = librariesNavigationRoute) {
        LibrariesScreen(
            onNavigateUp = onNavigateUp,
        )
    }
}

fun NavGraphBuilder.logsScreen(
    onNavigateUp: () -> Unit,
) {
    composable(route = logsNavigationRoute) {
        LogsScreen(
            onNavigateUp = onNavigateUp,
        )
    }
}
