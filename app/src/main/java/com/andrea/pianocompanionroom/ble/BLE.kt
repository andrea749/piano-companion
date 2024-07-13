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
import java.util.UUID

/* This class holds references to specific devices and characteristics */
class BLE(
    private val targetDeviceName: String,
    private val serviceUUID: String,
    private val characteristicUUID: String,
) {
    var bluetoothGatt: BluetoothGatt? = null
    var device: BluetoothDevice? = null

    @SuppressLint("MissingPermission")
    fun scanAndConnectToTarget(
        context: Context,
        onCharacteristicWrite: () -> Unit,
        firstByteArray: ByteArray,
        onScanResult: () -> Unit = {},
        onScanFailed: () -> Unit = {},
        ) {
        getScanner(context).startScan(getScanCallback(onScanResult, onScanFailed))
        device?.connectGatt(context, false, getBluetoothGattCallback(onCharacteristicWrite))
        writeCharacteristic(firstByteArray)
    }
    private fun getScanner(context: Context): BluetoothLeScanner {
        val bluetooth = context.getSystemService(Context.BLUETOOTH_SERVICE)
                as? BluetoothManager
            ?: throw Exception("Bluetooth is not supported by this device")
        return bluetooth.adapter.bluetoothLeScanner
    }

    // optional for logging maybe
    private fun getScanCallback(
        onScanResult: () -> Unit = {},
        onScanFailed: () -> Unit = {},
    ): ScanCallback {
        return object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                onScanResult()
                if (result != null && result.device.name == targetDeviceName) {
                    device = result.device
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                onScanFailed()
            }
        }
    }

    private fun getBluetoothGattCallback(onCharacteristicWrite: () -> Unit): BluetoothGattCallback {
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

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                // can check for specific characteristics here but we only have 1 so not passing
                // to fn below for now
                onCharacteristicWrite()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun writeCharacteristic(
        payload: ByteArray
    ) {
        bluetoothGatt?.let {
            val ledServiceUuid = UUID.fromString(serviceUUID)
            val ledCharUuid = UUID.fromString(characteristicUUID)
            val characteristic = it.getService(ledServiceUuid)?.getCharacteristic(ledCharUuid)

            // use these deprecated fns to accommodate older APIs
            characteristic?.value = payload
            it.writeCharacteristic(characteristic)
        } ?: error("Not connected to a device")
    }
}