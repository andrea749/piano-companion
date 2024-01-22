package com.sample.ble.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sample.ble.MainActivity
import com.sample.ble.MainActivity.Companion.RUNTIME_PERMISSION_REQUEST_CODE
import com.sample.ble.MainActivity.Companion.ENABLE_MICROPHONE_REQUEST_CODE
import com.sample.ble.R
import java.util.UUID

fun Context.hasPermission(permissionType: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permissionType) ==
            PackageManager.PERMISSION_GRANTED
}
fun Context.hasRequiredRuntimePermissions(requestCode: Int): Boolean {
    return when(requestCode) {
        RUNTIME_PERMISSION_REQUEST_CODE -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                        hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        ENABLE_MICROPHONE_REQUEST_CODE -> {
            hasPermission(Manifest.permission.RECORD_AUDIO)
        }
        else -> throw IllegalStateException("incorrect request code")
    }
}

fun Activity.requestRelevantRuntimePermissions(requestCode: Int) {
    if (hasRequiredRuntimePermissions(requestCode)) { return }
    when (requestCode) {
        RUNTIME_PERMISSION_REQUEST_CODE -> {
            when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                    requestLocationPermission(this)

                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    requestBluetoothPermissions(this)
                }
            }
        }
        ENABLE_MICROPHONE_REQUEST_CODE -> {
            requestMicrophonePermission(this)
        }
    }
}

fun requestMicrophonePermission(activity: Activity) {
    activity.runOnUiThread {
        val alert = AlertDialog.Builder(activity)
        alert.apply {
            setTitle("Microphone permission required")
            setMessage("Microphone access required for note detection feature.")
            setPositiveButton(android.R.string.ok) { _, _ ->
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    ENABLE_MICROPHONE_REQUEST_CODE
                )
            }
        }.show()
    }
}

fun requestLocationPermission(activity: Activity) {
    activity.runOnUiThread {
        val alert = AlertDialog.Builder(activity)
        alert.apply {
            setTitle("Location permission required")
            setMessage("Starting from Android M (6.0), the system requires apps to be granted " +
                    "location access in order to scan for BLE devices.")
            setPositiveButton(android.R.string.ok) { _, _ ->
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    RUNTIME_PERMISSION_REQUEST_CODE
                )
            }
        }.show()
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun requestBluetoothPermissions(activity: Activity) {
    activity.runOnUiThread {
        val alert = AlertDialog.Builder(activity)
        alert.apply {
            setTitle("Bluetooth permissions required")
            setMessage("Starting from Android 12, the system requires apps to be granted " +
                    "Bluetooth access in order to scan for and connect to BLE devices.")
            setPositiveButton(android.R.string.ok) { _, _ ->
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    RUNTIME_PERMISSION_REQUEST_CODE
                )
            }
        }.show()
    }
}

fun MainActivity.startBleScan() {
    Log.d("sammy", "startBleScan")

    if (!this.hasRequiredRuntimePermissions(RUNTIME_PERMISSION_REQUEST_CODE)) {
        Log.d("sammy", "does Not have required Runtime Permission")
        this.requestRelevantRuntimePermissions(RUNTIME_PERMISSION_REQUEST_CODE)
    } else {
        Log.d("sammy", "YES it has required Runtime Permission")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ) {
            return
        }
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)// short scan periods
            .setMatchMode(ScanSettings.MATCH_MODE_STICKY) // only nearby devices
            .build()
        
        bleScanner.startScan(null, scanSettings, scanCallback)
        isScanning = true
    }
}

fun MainActivity.stopBleScan() {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_SCAN
        ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    ) {
        // TODO: replace with security exception
        return
    }
    bleScanner.stopScan(scanCallback)
    isScanning = false
}

fun MainActivity.checkAudioPermission() {
    if (!this.hasRequiredRuntimePermissions(ENABLE_MICROPHONE_REQUEST_CODE)) {
        requestRelevantRuntimePermissions(ENABLE_MICROPHONE_REQUEST_CODE)
    }
}

fun BluetoothGatt.printGattTable() {
    if (services.isEmpty()) {
        Log.i("printGattTable", "No service and characteristic available, call discoverServices() first?")
        return
    }
    services.forEach { service ->
        val characteristicsTable = service.characteristics.joinToString(
            separator = "\n|--",
            prefix = "|--"
        ) { it.uuid.toString() }
        Log.i("printGattTable", "\nService ${service.uuid}\nCharacteristics:\n$characteristicsTable"
        )
    }
}

fun BluetoothGattCharacteristic.isReadable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

fun BluetoothGattCharacteristic.isWritable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
    return properties and property != 0
}

fun ByteArray.toHexString(): String =
    joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }

fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)


