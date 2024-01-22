package com.sample.ble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.sample.ble.presentation.Navigation
import com.sample.ble.ui.theme.BleTheme
import com.sample.ble.util.hasPermission
import com.sample.ble.util.hasRequiredRuntimePermissions
import com.sample.ble.util.requestRelevantRuntimePermissions
import com.sample.ble.util.startBleScan
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // TODO: handle all the permission checks and try/catches

    @Inject lateinit var bluetoothAdapter: BluetoothAdapter
    @Inject lateinit var bleScanner : BluetoothLeScanner
    @Inject lateinit var scanCallback: ScanCallback

    var isScanning = false
        set(value) {
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleTheme {
                Navigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    @SuppressLint("MissingPermission")
    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            ) {
                return
            }
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("sammy", "onRequestPermissionResult (MainActivity 83)")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val containsPermanentDenial = permissions.zip(grantResults.toTypedArray()).any {
            it.second == PackageManager.PERMISSION_DENIED &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, it.first)
        }
        val containsDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
        val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        when {
            containsPermanentDenial -> {
                Log.d("sammy", "containsPermanentDenial")
                // TODO: Handle permanent denial (e.g., show AlertDialog with justification)
                // Note: The user will need to navigate to App Settings and manually grant
                // permissions that were permanently denied
            }
            containsDenial -> {
                Log.d("sammy", "Contains Denial")
                requestRelevantRuntimePermissions(requestCode)
            }
            allGranted && hasRequiredRuntimePermissions(requestCode) -> {
                Log.d("sammy", "Look Andrea, it's working!!!")
                if (requestCode == RUNTIME_PERMISSION_REQUEST_CODE) {
                    startBleScan()
                }
            }
            else -> {
                // Unexpected scenario encountered when handling permissions
                Log.d("sammy", "else else else recreate()")
                recreate()
            }
        }
    }

    companion object {
        const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
        const val RUNTIME_PERMISSION_REQUEST_CODE = 2
        const val ENABLE_MICROPHONE_REQUEST_CODE = 3

    }
}

