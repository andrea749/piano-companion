package com.andrea.pianocompanionroom.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrea.pianocompanionroom.R
import com.andrea.pianocompanionroom.ui.navigation.NavigationDestination
import com.andrea.pianocompanionroom.ui.theme.ThemeColors
import com.andrea.pianocompanionroom.viewmodel.ALL_BLE_PERMISSIONS
import com.andrea.pianocompanionroom.viewmodel.HomeViewModel

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_screen
}

@Composable
fun HomeScreen(
    navigateToMidiRoom: () -> Unit,
    navigateToUploadScreen: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!viewModel.permissionsUiState.value) {
                GrantPermissionsButton(onPermissionGranted = {viewModel.setPermissionsUiState(true)})
            } else {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                    Button(
                        onClick = navigateToMidiRoom,
                        colors = ThemeColors.NavigationButtonColors,
                        border = ThemeColors.NavigationButtonBorderStroke,
                        ) {
                        Text(
                            text = "Midi Room",
                            color = ThemeColors.ButtonTextColor,
                            fontSize = 20.sp,
                            )
                    }
                    Button(
                        onClick = navigateToUploadScreen,
                        colors = ThemeColors.NavigationButtonColors,
                        border = ThemeColors.NavigationButtonBorderStroke,
                        ) {
                        Text(text = "Upload",
                            color = ThemeColors.ButtonTextColor,
                            fontSize = 20.sp,
                            )
                    }
                }
            }
        }
    }
}

@Composable
fun GrantPermissionsButton(onPermissionGranted: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.all { it }) {
            onPermissionGranted()
        }
        else {
            // retry permissions, maybe add a toast directing user to app settings
        }
    }
    Button(
        onClick = { launcher.launch(ALL_BLE_PERMISSIONS) },
    ) {
        Text("Grant Permission")
    }
}
