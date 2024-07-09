package com.andrea.pianocompanionroom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.andrea.pianocompanionroom.view.MidiRoomDestination
import com.andrea.pianocompanionroom.view.MidiRoomScreen

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
            MidiRoomScreen()
        }
    }
}