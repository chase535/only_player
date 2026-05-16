package one.only.player.navigation

import android.content.Context
import android.content.Intent
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import one.only.player.MainActivity
import one.only.player.feature.player.PlayerActivity
import one.only.player.feature.player.service.PlayerService
import one.only.player.feature.videopicker.navigation.MediaPickerRoute
import one.only.player.feature.videopicker.navigation.MediaPickerScreenMode
import one.only.player.feature.videopicker.navigation.mediaPickerScreen
import one.only.player.feature.videopicker.navigation.navigateToCloudHome
import one.only.player.feature.videopicker.navigation.navigateToMediaPickerScreen
import one.only.player.feature.videopicker.navigation.navigateToRecycleBinScreen
import one.only.player.feature.videopicker.navigation.navigateToSearch
import one.only.player.feature.videopicker.navigation.searchScreen
import one.only.player.settings.navigation.navigateToSettings

@Serializable
data object MediaRootRoute

fun NavGraphBuilder.mediaNavGraph(
    context: Context,
    navController: NavHostController,
) {
    navigation<MediaRootRoute>(startDestination = MediaPickerRoute()) {
        mediaPickerScreen(
            onNavigateUp = navController::navigateUp,
            onNavigateHome = {
                navController.popBackStack(MediaPickerRoute(), inclusive = false)
            },
            onSettingsClick = navController::navigateToSettings,
            onPlayVideo = { uri ->
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = uri
                }
                context.startActivity(intent)
            },
            onFolderClick = { folderPath, screenMode ->
                navController.navigateToMediaPickerScreen(
                    folderId = folderPath,
                    screenMode = screenMode,
                )
            },
            onRecycleBinClick = navController::navigateToRecycleBinScreen,
            onSearchClick = navController::navigateToSearch,
            onCloudClick = navController::navigateToCloudHome,
            onExitAppClick = {
                context.stopService(Intent(context, PlayerService::class.java))
                navController.popBackStack(MediaPickerRoute(), inclusive = false)
                (context as? MainActivity)?.finishAffinity()
            },
        )

        searchScreen(
            onNavigateUp = navController::navigateUp,
            onPlayVideo = { uri ->
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = uri
                }
                context.startActivity(intent)
            },
            onFolderClick = { folderPath ->
                navController.navigateToMediaPickerScreen(
                    folderId = folderPath,
                    screenMode = MediaPickerScreenMode.LIBRARY,
                )
            },
        )
    }
}
