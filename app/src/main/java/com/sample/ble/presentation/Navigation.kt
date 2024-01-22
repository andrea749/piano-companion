package com.sample.ble.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.BluetoothRequest.route,
    ) {
    composable(Screen.StartScreen.route) {
        StartScreen(
            navController = navController,
        )
    }
    composable(Screen.AudioCaptureScreen.route) {
        AudioCaptureScreen(
            navController = navController
        )
    }
    composable(Screen.BluetoothRequest.route) {
        BluetoothRequestScreen(
            navController = navController
        )
    }
}

}

sealed class Screen(val route: String) {
    object StartScreen: Screen("start_screen")
    object AudioCaptureScreen: Screen("audio_capture_screen")
    object BluetoothRequest: Screen("bluetooth_request")
}