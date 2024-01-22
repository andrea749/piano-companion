package com.sample.ble.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sample.ble.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID

class getBluetoothGattCallback(
    val context: ApplicationContext,
    var bluetoothGatt: BluetoothGatt?,
    var currByte: Int,
): BluetoothGattCallback()  {


    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            val deviceAddress = gatt?.device?.address
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    bluetoothGatt = gatt
                    Handler(Looper.getMainLooper()).post {
                        try {
                            bluetoothGatt?.discoverServices()

                        } catch (e: SecurityException) {
                            Log.w("andrea", "SecurityException at gatt.discoverServices")
                        }

                    }
                    // successfully connected to the GATT Server
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("andrea", "disconnected from $deviceAddress")
                    // disconnected from the GATT Server
                    try {
                        gatt?.close()
                    } catch (e: SecurityException) {
                        Log.w("andrea", "SecurityException on gatt.close()")
                    }
                }
            } else {
                Log.w("andrea", "Error $status encountered for $deviceAddress")
                try {
                    gatt?.close()
                } catch (e: SecurityException) {
                    Log.w("andrea", "SecurityException on gatt.close()")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.let {
                Log.w(
                    "andrea",
                    "discovered ${it.services?.size} services for ${it.device?.address}"
                )
                it.printGattTable()
                // connection setup is complete
            }
//            val ledChar = getLedWriteCharacteristic(context)
//            // TODO: test setting notifs
//            ledChar?.let {
//                if (it.isIndicatable() && it.isNotifiable()) {
//                    // TODO: test with false set
//                    val setNotif = gatt?.setCharacteristicNotification(it, true)
//                }
//            }
        }

        // not really needed
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i(
                            "BluetoothGattCallback",
                            "Read characteristic $uuid:\n${value.toHexString()}"
                        )
                    }

                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Log.e("BluetoothGattCallback", "Read not permitted for $uuid!")
                    }

                    else -> {
                        Log.e(
                            "BluetoothGattCallback",
                            "Characteristic read failed for $uuid, error: $status"
                        )
                    }
                }
            }
        }

        /**
         * For writes without response, if the write is a one-off then there’s likely nothing to
         * worry about. But if you’re doing back-to-back writes, it’s probably a good idea to pace
         * your writes with onCharacteristicWrite if you do get it, or have the writes be spaced out
         * by a timer that is roughly equivalent to the connection interval if you don’t see
         * onCharacteristicWrite being delivered.
         */
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.d("andrea", "onCharWrite in VM")
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i(
                            "BluetoothGattCallback",
                            "Wrote to characteristic $uuid | value: ${value.toHexString()}"
                        )
                        // send next bytes here
                        // and fix references
//                        if (currByte < payload.size - 1) {
//                            currByte += 1
//                            writeCharacteristic(characteristic, payload)
//                        } else {
//                            Log.d("andrea", "should be done??")
//                            currByte = 0
//                            try {
//                                gatt.disconnect()
//                            } catch (e: SecurityException) {
//                                Log.d("andrea", "SecExc @ vm.onCharWrite")
//                            }
//                        }
                    }

                    BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> {
                        Log.e("BluetoothGattCallback", "Write exceeded connection ATT MTU!")
                    }

                    BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                        Log.e("BluetoothGattCallback", "Write not permitted for $uuid!")
                    }

                    else -> {
                        Log.e(
                            "BluetoothGattCallback",
                            "Characteristic write failed for $uuid, error: $status"
                        )
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic) {
                Log.i(
                    "BluetoothGattCallback",
                    "Characteristic $uuid changed | value: ${value.toHexString()}"
                )
            }
        }
    }

    /**
     * UUIDs are set in the arduino code
     * service = device
     * characteristic = property of device
     */
    private fun getLedWriteCharacteristic(): BluetoothGattCharacteristic? {
        // hopefully this works, I think it uses phone settings to determine language
        val system: Resources = Resources.getSystem()
        val ledServiceUuid = UUID.fromString(system.getString(R.string.led_service_uuid))
        val ledCharUuid = UUID.fromString(system.getString(R.string.led_char_uuid))
        return bluetoothGatt?.getService(ledServiceUuid)?.getCharacteristic(ledCharUuid)
    }

//    private fun getPayload()
}