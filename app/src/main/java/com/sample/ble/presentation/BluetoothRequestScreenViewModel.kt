package com.sample.ble.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.ble.R
import com.sample.ble.di.AppModule
import com.sample.ble.util.isIndicatable
import com.sample.ble.util.isNotifiable
import com.sample.ble.util.isReadable
import com.sample.ble.util.isWritable
import com.sample.ble.util.isWritableWithoutResponse
import com.sample.ble.util.parseMidiFile
import com.sample.ble.util.printGattTable
import com.sample.ble.util.toHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BluetoothRequestScreenViewModel @Inject constructor(
    val context: Context, // AppContext is injected so might be ok??
): ViewModel() {
    // TODO: move bluetooth logic into separate class

    private val _scanResultsFlow = MutableStateFlow<UiState>(UiState.Loading)
    val scanResultsFlow = _scanResultsFlow.asStateFlow()
    private val allResults = mutableListOf<ScanResult>()
    var bluetoothGatt: BluetoothGatt? = null
    var payload: Array<ByteArray> = arrayOf()
    var currByte = 0 // track which byte we are currently sending


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
                Log.w("andrea", "discovered ${it.services?.size} services for ${it.device?.address}")
                it.printGattTable()
                // connection setup is complete
            }
            val ledChar = getLedWriteCharacteristic(context)
            // TODO: test setting notifs
            ledChar?.let {
                if (it.isIndicatable() && it.isNotifiable()) {
                    // TODO: test with false set
                    val setNotif = gatt?.setCharacteristicNotification(it, true)
                }
            }
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
                        Log.i("BluetoothGattCallback", "Read characteristic $uuid:\n${value.toHexString()}")
                    }
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Log.e("BluetoothGattCallback", "Read not permitted for $uuid!")
                    }
                    else -> {
                        Log.e("BluetoothGattCallback", "Characteristic read failed for $uuid, error: $status")
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
                        Log.i("BluetoothGattCallback", "Wrote to characteristic $uuid | value: ${value.toHexString()}")
                        // send next bytes here
                        if (currByte < payload.size-1) {
                            currByte += 1
                            writeCharacteristic(characteristic, payload)
                        } else {
                            Log.d("andrea", "should be done??")
                            currByte = 0
                            try {
                                gatt.disconnect()
                            } catch (e: SecurityException) {
                                Log.d("andrea", "SecExc @ vm.onCharWrite")
                            }
                        }
                    }
                    BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> {
                        Log.e("BluetoothGattCallback", "Write exceeded connection ATT MTU!")
                    }
                    BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                        Log.e("BluetoothGattCallback", "Write not permitted for $uuid!")
                    }
                    else -> {
                        Log.e("BluetoothGattCallback", "Characteristic write failed for $uuid, error: $status")
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic) {
                Log.i("BluetoothGattCallback", "Characteristic $uuid changed | value: ${value.toHexString()}")
            }
        }
    }


    fun addScanResults(newDevice: ScanResult?) {
        if (_scanResultsFlow.value is UiState.Success) {
            Log.d("sammy", "flow size = ${(_scanResultsFlow.value as UiState.Success).list.size}")

        }
        try {
            // TODO: add scan filter and delete this
            if (newDevice?.device?.name == "ARDUINO") {
                allResults.add(newDevice)
            }
        } catch (e: SecurityException) {
            Log.d("andrea", "security exception")
        }

        _scanResultsFlow.value = UiState.Success((allResults as List<ScanResult>))

    }

    fun clearScanResults() {
        allResults.clear()
    }

    // connect to device and send 0x01 for testing. later this will just connect and 'writeCharacteristic' will be called elsewhere
    fun connectGatt(result: ScanResult, _context: Context) {
        try {
            result.device.connectGatt(_context, false, bluetoothGattCallback)
        } catch (e: SecurityException) {
            Log.e("andrea", "SecurityException at connectGatt")
        }
    }

    private fun getLedWriteCharacteristic(_context: Context): BluetoothGattCharacteristic? {
        val ledServiceUuid = UUID.fromString(_context.getString(R.string.led_service_uuid))
        val ledCharUuid = UUID.fromString(_context.getString(R.string.led_char_uuid))
        return bluetoothGatt?.getService(ledServiceUuid)?.getCharacteristic(ledCharUuid)
    }

// TODO: pass serialized json here
//    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, payload: ByteArray) {
    private fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, payload: Array<ByteArray>) {
        val writeType = when {
            characteristic.isWritable() -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            characteristic.isWritableWithoutResponse() -> {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }
            else -> error("Characteristic ${characteristic.uuid} cannot be written to")
        }

        bluetoothGatt?.let { gatt ->
            characteristic.writeType = writeType
            characteristic.value = payload[currByte]
//                characteristic.value = payload
            try {
                // move this into the onCharWrite callback
                // does this kick off onCharacteristicWrite or can we move it like line above ^
                gatt.writeCharacteristic(characteristic)
            } catch (_: SecurityException) {
                Log.d("andrea", "security exception in writeChar")
            }
        } ?: error("Not connected to a BLE device!")
    }

    fun getAndWriteToChar(_context: Context) {
        val bleGattCharacteristic : BluetoothGattCharacteristic? = getLedWriteCharacteristic(_context)
        bleGattCharacteristic?.let {
//            val payload: Array<ByteArray> = parseMidiFile(_context)
            payload = parseMidiFile()
            writeCharacteristic(bleGattCharacteristic, payload)
        }
    }

    private fun writeDescriptor(descriptor: BluetoothGattDescriptor, payload: ByteArray) {
        bluetoothGatt?.let { gatt ->
            descriptor.value = payload
            try {
                gatt.writeDescriptor(descriptor)
            } catch (e: SecurityException) {
                Log.d("andrea", "SecurityException at vm.writeDescriptor")
            }
        } ?: error("Not connected to a BLE device!")
    }

    fun enableNotifications(_context: Context, characteristic: BluetoothGattCharacteristic) {
        val cccdUuid = UUID.fromString(_context.getString(R.string.CCCD_uuid))
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> {
                Log.e("ConnectionManager", "${characteristic.uuid} doesn't support notifications/indications")
                return
            }
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
            var notifEnabled = false
            try{
                bluetoothGatt?.setCharacteristicNotification(characteristic, true)?.let {
                    notifEnabled = it
                }
            } catch (e: SecurityException) {
                Log.d("andrea", "securityException @ enableNotif")
            }
            if (!notifEnabled) {
                Log.e("ConnectionManager", "setCharacteristicNotification failed for ${characteristic.uuid}")
                return
            }
            writeDescriptor(cccDescriptor, payload)
        } ?: Log.e("ConnectionManager", "${characteristic.uuid} doesn't contain the CCC descriptor!")
    }

    fun disableNotifications(_context:Context, characteristic: BluetoothGattCharacteristic) {
        if (!characteristic.isNotifiable() && !characteristic.isIndicatable()) {
            Log.e("ConnectionManager", "${characteristic.uuid} doesn't support indications/notifications")
            return
        }

        val cccdUuid = UUID.fromString(_context.getString(R.string.CCCD_uuid))
        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
            var disableNotif = false
            try {
                bluetoothGatt?.setCharacteristicNotification(characteristic, false)?.let {
                    disableNotif = it
                }
            } catch (e: SecurityException) {
                Log.d("andrea", "secExcep @ disableNotif")
            }
            if (!disableNotif) {
                Log.e("ConnectionManager", "setCharacteristicNotification failed for ${characteristic.uuid}")
                return
            }
            writeDescriptor(cccDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        } ?: Log.e("ConnectionManager", "${characteristic.uuid} doesn't contain the CCC descriptor!")
    }
}



data class BluetoothListState(
    val scanResults: MutableList<ScanResult> = mutableListOf()
)

sealed class UiState() {
    class Success(val list: List<ScanResult>): UiState()
    object Loading: UiState()
}
