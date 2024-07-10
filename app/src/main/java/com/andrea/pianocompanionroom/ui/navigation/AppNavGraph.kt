package com.andrea.pianocompanionroom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrea.pianocompanionroom.view.MidiRoomDestination
import com.andrea.pianocompanionroom.view.MidiRoomScreen
import com.andrea.pianocompanionroom.view.MidiUploadScreen
import com.andrea.pianocompanionroom.view.MidiUploadDestination
import com.andrea.pianocompanionroom.view.ViewMidiDestination
import com.andrea.pianocompanionroom.view.ViewMidiScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController, startDestination = MidiRoomDestination.route, modifier = modifier
    ) {
        composable(route = MidiRoomDestination.route) {
            MidiRoomScreen(
                navigateToUploadScreen = { navController.navigate(MidiUploadDestination.route) },
                navigateToViewScreen = { navController.navigate("${ViewMidiDestination.route}/$it") }
            )
        }
        composable(route = MidiUploadDestination.route) {
            MidiUploadScreen(navigateToMidiRoom = { navController.navigate(MidiRoomDestination.route) })
        }
        composable(
            route = ViewMidiDestination.routeWithArgs,
            arguments = listOf(navArgument(ViewMidiDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ViewMidiScreen(navigateToMidiRoom = { navController.navigate(MidiRoomDestination.route) })
        }
    }
}