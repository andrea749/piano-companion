package com.sample.ble.presentation

import android.Manifest
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sample.ble.MainActivity
import com.sample.ble.util.checkAudioPermission
import com.sample.ble.util.hasPermission
import com.sample.ble.util.parseMidiFile
import com.sample.ble.util.startBleScan
import com.sample.ble.util.stopBleScan
import java.util.concurrent.ScheduledThreadPoolExecutor

@Composable
fun BluetoothRequestScreen(
    vm: BluetoothRequestScreenViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current as MainActivity
    val scanResults = vm.scanResultsFlow.collectAsState()
    if (scanResults.value is UiState.Success) {
        Log.d("sammy", "size in composable = ${(scanResults.value as UiState.Success).list.size}. id = ${scanResults.value.hashCode()}")
    }
    Column(modifier=Modifier.fillMaxWidth()) {
        Text(
            text = "Piano Roll"
        )
        Button(
            onClick = {
                if (context.isScanning) {
                    context.stopBleScan()
                } else {
                    vm.clearScanResults()
                    context.startBleScan()
                }

            }) {
            Text(
                text = if (context.isScanning) "Stop bluetooth scan" else "Start bluetooth scan"
            )
        }
        Button(onClick = {
                vm.getAndWriteToChar(context)
            }) {
                Text(text = "parse and send file!")
        }
        if (scanResults.value is UiState.Success) {
            ScanResult((scanResults.value as UiState.Success).list, vm = vm)
        }
        Button(onClick = {
                context.checkAudioPermission()
                if (context.hasPermission(Manifest.permission.RECORD_AUDIO)) {
                    navController.navigate(Screen.AudioCaptureScreen.route)
                }
        }) {
            Text(text = "go to audio capture")
            // TODO: grey out button if permission not granted
        }
    }
}

@Composable
private fun ScanResult(
    devices: List<ScanResult>,
    modifier: Modifier = Modifier,
    vm: BluetoothRequestScreenViewModel,
) {
    Log.d("sammy", "list =${devices.size}")
    var deviceName = "Unnamed"
    val context = LocalContext.current as MainActivity

    LazyColumn {
        items(devices) { result ->
            Row(
                Modifier.clickable {
                    vm.connectGatt(result, context)
                    if (context.isScanning) {
                        context.stopBleScan()
                    }
                }
            ) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                ) {
                    deviceName = result.device.name ?: "Unnamed"
                }
                Text(
                    text = deviceName,
                    )
                Text(
                    text = result.device.address ?: "No address",
                    )
                Text(
                    text = "${result.rssi} dBm"
                    )
            }

        }
    }
}



