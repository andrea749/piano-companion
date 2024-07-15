## Welcome to the WIP piano companion app!  
This app requires bluetooth compatability.  
### How it works:  
The first screen the user sees is the Home screen. This screen handles asking for bluetooth permissions and navigating between two screens. Once permissions are granted, the user can go to either the MidiRoomScreen or the MidiUploadScreen.

<img src="/HomeScreen.png" width="224" height="498">

### Upload Screen
The user enters this screen either by selecting "upload" in the home screen or by selecting "upload file" in the MidiRoom screen.

<img src="/selectFlow.png" width="155" height="350"> <img src="/MidiUploadScreen.png" width="224" height="498">

The "select file" button opens the Android file picker, narrowed down to just .mid files. 
From there, it passes the InputStream through convertMidiToSong. This turns all Note On and Note Off events and turns each MIDI Event into an array with 3 numbers: 
[tick (timestamp), pitch, velocity (volume)]
This, along with user inputted information, is stored in the SongsRepository via insertSong.

### Midi Room
The user enters this page either after selecting "finished" in the upload screen or by selecting "Midi Room" in the home screen.  
<img src="/uploadFlow.png" width="165" height="188"><img src="/MidiRoomScreen.png" width="224" height="498">  
Here, all the songs stored in the local database are displayed in clickable cards. There is a search bar above the list and it narrows down song cards as the user starts typing their query.
When the user selects a song, the song ID is sent to ViewMidiScreen via the NavigationDestination.

### ViewMidiScreen
<img src="/ViewMidiScreen.png" width="224" height="498">
A misnomer, this doesn't display a MIDI file but rather holds controls for streaming to the arudino. Currently, an animated countdown displays and then the song is sent via bluetooth. The play/pause buttons, along with rewind/ff, are not operational currently.

### Navigation
This app uses Compose Navigation. Each screen has an associated NavigationDestination object, which defines the path to the screen and any additional arguments. For navigating to the ViewMidiScreen, a song ID is passed from the MidiRoomScreen via an onClick function created in the AppNavGraph and then used in the ViewMidiViewModel to retrieve the song from the app. 

### Room
This app uses Room to manage the database.  
  
#### SongInventoryDatabase  
* defines what type of object is in the table, what (if any) TypeConverters are needed, and what migration strategy should be used. I used destructiveMigration but will probably change this later
  
#### OfflineSongsRepository  
* implements the functions in SongsRepository, calls on the SongDao
  
#### SongDao  
* has all the queries. Some are provided by Room, others (like all the GET functions) are written here
  
#### Song  
* has a primaryKey (required by Room) and some fake data. Song.notes is of type List\<List\<Number\>\> since the "note" might contain ints (for pitch or velocity) or floats (for tick). Number is not a type that Room can automatically store in the db, so MidiEventConverter is required to convert this between List\<List\<Number\>\> <-> String
  
#### MidiUtil    
* parseMidiFile converts a MIDI file into a Song, making it easy to store in the db.

### DI
Hilt is used to inject dependencies, and providers are defined in the AppModule.  
wip: The BLE class might be broken down and injected later.

### BLE
Currently a majority of the code sits in the BLE class, and any users need to create an instance and pass in a service UUID, characteristic UUID, and name of the device to connect to. (My UUIDs are set in the arduino code.) For the purposes of this project, all that needs to be done is connect to the specified device and send a ByteStream and occasionally some other bytes (the behavior of play/pause is TBD). Because of this, the main BLE API is scanAndConnnectToTarget and writeCharacteristic.  
##### scanAndConnectToTarget:
* Params
  * context
  * onCharacteristicWrite: called when the receiving device is successfully written to
  * firstByteArray: to kick off the writes
  * onScanResult: called when scanning for devices. If you wanted to display all the detected devices, this would be a good place to pass any detected devices into a Flow. For now, since we know what to connect to, nothing is passed.
  * onScanFailed: could pass in some logging here
  
#### General set up:    
  * get BluetoothLeScanner
  * get scanCallback
    * here, we save a reference to the device we want to connect to later. If you wanted to display all devices, you could add to a Flow here and then save the reference once the user picks the device.
  * start scan
  * get BluetoothGattCallback
    * here we watch out for the connection state. When the state is STATE_CONNECTED, we keep a reference to the BluetoothGatt (connection to the device) so we can read/write/etc later.
      * gett.discoverServices() returns all the services the connected device offers. Since I know exactly what service has the characteristic I want to write to, I'm not really using this but leaving it for now. If you wanted, you could display the services and also get any available characteristics for each service here.
    * onCharacteristicWrite
      * if we had multiple characteristics, we'd need to check which one was just written to here. Since there's only one, we automatically call onCharacteristicWrite, which in this case just sends the next piece of data to the device.
  
##### Song.kt  
* has a NumberSerializer. We'll never need to decode things since we only send, so only serialize is implemented. Everything else in Song is either a String or has a serializer built in.
  
##### MidiUtil  
* gets all the NoteOn/NoteOff events from the MIDI file, converts to a ByteArray, and then chunks it into 32-byte sized arrays, ready to be sent via bluetooth!




