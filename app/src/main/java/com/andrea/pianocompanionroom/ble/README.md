## BLE deep(ish) dive  
In this app, BLE is used to send JSON to an arduino, which in turn processes the JSON and displays it on an LED strip. 
![Code here](https://github.com/andrea749/music-lights)  
### Usage  
BLE is created in ![SongsApplication.kt](https://github.com/andrea749/piano-companion/blob/main/app/src/main/java/com/andrea/pianocompanionroom/SongsApplication.kt).
This is so that once a device is found and a connection is established, we can continue to use that for the rest of the time the app is active.
BLE is used in ![ViewMidiViewModel](https://github.com/andrea749/piano-companion/blob/main/app/src/main/java/com/andrea/pianocompanionroom/viewmodel/ViewMidiViewModel.kt).
Here, we call the main BLE API, `scanAndConnectToTarget()`.

### BLE details
For the purposes of this project, all that needs to be done is connect to the specified device and send a ByteStream and occasionally some other bytes (the behavior of play/pause is TBD). Because of this, the main BLE API is scanAndConnnectToTarget and writeCharacteristic.  
The BLE class contains the name of the target device, the service UUID, and the characteristic UUID, all of which are set in the Arduino's code.  

##### scanAndConnectToTarget:
* Params
  * onCharacteristicWrite: called when the receiving device is successfully written to. We use this instead of just continuously calling `writeCharacteristic()` because it ensures that the device has received the previous thing we wrote.
  * firstByteArray: to kick off the writes and trigger `onCharacteristicWrite` for subsequent ByteArrays
  * onScanResult: called when scanning for devices. If you wanted to display all the detected devices, this would be a good place to pass any detected devices into a Flow. For now, since we know what to connect to, nothing is passed and we automatically connect to the target device and stop scanning.
  * onScanFailed: could pass in some logging here
  
#### General set up:    
  * get BluetoothLeScanner
  * get scanCallback
    * here, we save a reference to the device we want to connect to later. If you wanted to display all devices found during scanning, you could add to a Flow here and then save a reference to an exact device once the user picks the device.
  * start scan
  * get BluetoothGattCallback
    * here we watch out for the connection state. When the state is STATE_CONNECTED, we keep a reference to the BluetoothGatt (connection to the device) so we can read/write/etc later.
      * gatt.discoverServices() returns all the services the connected device offers. You can loop through the services and log the associated characteristics. Even if you know exactly which service and characteristic you want to connect to, `discoverServices()` is necessary or else the ![characteristic here](https://github.com/andrea749/piano-companion/blob/19922725852eaa8d4f12d4bb45db9daf9919b7a8/app/src/main/java/com/andrea/pianocompanionroom/ble/BLE.kt#L134) will be null.
    * onCharacteristicWrite
      * if we had multiple characteristics, we'd need to check which one was just written to here. Since there's only one, we automatically call onCharacteristicWrite, which in this case just sends the next piece of data to the device.

#### Helper files  

##### Song.kt  
* has a NumberSerializer. We'll never need to decode things since we only send, so only serialize is implemented. Everything else in Song is either a String or has a serializer built in. This serializer allows us to ![serialize](https://github.com/andrea749/piano-companion/blob/19922725852eaa8d4f12d4bb45db9daf9919b7a8/app/src/main/java/com/andrea/pianocompanionroom/data/MidiUtil.kt#L60) this class automatically. 
  
##### MidiUtil  
* ![gets](https://github.com/andrea749/piano-companion/blob/19922725852eaa8d4f12d4bb45db9daf9919b7a8/app/src/main/java/com/andrea/pianocompanionroom/data/MidiUtil.kt#L58) all the NoteOn/NoteOff events from the MIDI file, converts to a ByteArray, and then chunks it into 32-byte sized arrays, ready to be sent via bluetooth!
