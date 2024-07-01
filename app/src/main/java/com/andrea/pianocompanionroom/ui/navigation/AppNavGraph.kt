package com.andrea.pianocompanionroom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.andrea.pianocompanionroom.view.UploadMidiDestination
import com.andrea.pianocompanionroom.view.UploadMidiScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController, startDestination = UploadMidiDestination.route, modifier = modifier
    ) {
        composable(route = UploadMidiDestination.route) {
            UploadMidiScreen("andrea")
        }
    }
}