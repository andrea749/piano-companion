package com.andrea.pianocompanionroom.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat.getString
import com.andrea.pianocompanionroom.R
import java.util.UUID

/* This class holds references to specific devices and characteristics */
class BLE (
    private val context: Context,
    ) {
    private val targetDeviceName: String = getString(context, R.string.device_name)
    private val serviceUUID: String = getString(context, R.string.led_service_uuid)
    private val characteristicUUID: String = getString(context, R.string.led_char_uuid)
    var bluetoothGatt: BluetoothGatt? = null
    var device: BluetoothDevice? = null
    private val scanner: BluetoothLeScanner = getScanner(context)
    @SuppressLint("MissingPermission")
    fun scanAndConnectToTarget(
        onCharacteristicWrite: () -> Unit,
        firstByteArray: ByteArray,
        onScanResult: () -> Unit = {},
        onScanFailed: () -> Unit = {},
        ) {
        // if we need a BLE instance for different services/chars,
        // maybe move this callback out
        if (bluetoothGatt == null) {
            scanner.startScan(getScanCallback(
                onScanResult,
                onScanFailed,
                onCharacteristicWrite,
                firstByteArray
                )
            )
        } else {
            writeCharacteristic(firstByteArray)
        }
    }

    // optional for logging maybe
    private fun getScanCallback(
        onScanResult: () -> Unit = {},
        onScanFailed: () -> Unit = {},
        onCharacteristicWrite: () -> Unit = {},
        firstByteArray: ByteArray = byteArrayOf(0x01),
    ): ScanCallback {
        return object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                onScanResult()
                if (result != null && result.device.name == targetDeviceName) {
                    device = result.device
                    scanner.stopScan(this)
                    device?.connectGatt(context, false, getBluetoothGattCallback(onCharacteristicWrite, firstByteArray))
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                onScanFailed()
            }
        }
    }

    private fun getBluetoothGattCallback(onCharacteristicWrite: () -> Unit, firstByteArray: ByteArray = byteArrayOf(0x01)): BluetoothGattCallback {
        return object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS && gatt != null) {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            bluetoothGatt = gatt
                            Handler(Looper.getMainLooper()).post {
                                try { gatt.discoverServices() } catch (_: SecurityException) { }
                            }
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            try { gatt.close() } catch (_: SecurityException) { }
                        }
                    }
                } else {
                    try { gatt?.close() } catch (_: SecurityException) { }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                gatt?.services?.forEach { gattService ->
                    if (gattService.uuid.toString().lowercase() == serviceUUID.lowercase()) {
                        gattService.characteristics.forEach { char ->
                            if (char.uuid.toString().lowercase() == characteristicUUID.lowercase()) {
                                // we need to wait until the specific service and characteristic
                                // are found before we can write to them, or else they will be null
                                // in writeCharacteristic()
                                writeCharacteristic(firstByteArray)
                            }
                        }
                    }
                }
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                // can check for specific characteristics here but we only have 1 we write to so
                // not passing to fn below for now
                onCharacteristicWrite()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun writeCharacteristic(
        payload: ByteArray
    ) {
        bluetoothGatt?.let { gatt ->
            val ledServiceUuid = UUID.fromString(serviceUUID)
            val ledCharUuid = UUID.fromString(characteristicUUID)
            val characteristic = gatt.getService(ledServiceUuid)?.getCharacteristic(ledCharUuid)

            // use these deprecated fns to accommodate older APIs
            characteristic?.value = payload
            characteristic?.let { char ->
                gatt.writeCharacteristic(char)
            }
        } ?: error("Not connected to a device")
    }
}

private fun getScanner(context: Context): BluetoothLeScanner {
    val bluetooth = context.getSystemService(Context.BLUETOOTH_SERVICE)
            as? BluetoothManager
        ?: throw Exception("Bluetooth is not supported by this device")
    return bluetooth.adapter.bluetoothLeScanner
}
