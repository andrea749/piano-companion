## Welcome to the piano companion app!  

See it in action:  

https://github.com/user-attachments/assets/320e19f3-4685-462c-9de5-8c92a0aa24e1

### How it works:  
The first screen the user sees is the Home screen. This screen handles asking for bluetooth permissions and navigating between two screens. Once permissions are granted, the user can go to either the MidiRoomScreen or the MidiUploadScreen.

<img src="/img/home%20screen.png" width="224" height="498">

### Upload Screen
The user enters this screen either by selecting "upload" in the home screen or by selecting "select file" in the MidiRoom screen.

<img src="/img/selectFlow.png" width="155" height="350"> <img src="/img/upload%20screen.png" width="224" height="498">

The "select file" button opens the Android file picker, narrowed down to just .mid files. 
From there, it passes the InputStream through `convertMidiToSong`. This takes all Note On and Note Off events and turns each of them into an array with 3 numbers:  
[tick (timestamp), pitch, velocity (volume)]  
This, along with user inputted information, is stored in the SongsRepository via insertSong.

### Midi Room
The user enters this page either after selecting "finished" in the upload screen or by selecting "Midi Room" in the home screen.  
<img src="/img/uploadFlow.png" width="165" height="188"><img src="/img/midi%20room%20updated.png" width="224" height="498">  
Here, all the songs stored in the local database are displayed in clickable cards. There is a search bar above the list and it narrows down song cards as the user starts typing their query.
When the user selects a song, the song ID is sent to ViewMidiScreen via the NavigationDestination.

### ViewMidiScreen
<img src="/img/view%20midi%20updated.png" width="498" height="224">
A misnomer, this doesn't display a MIDI file but rather holds controls for streaming to the arudino. Currently, an animated countdown displays and then the song is sent via bluetooth. The play/pause buttons, along with rewind/ff, are not operational currently but will send a signal to the arduino.

### Navigation
This app uses Compose Navigation. Each screen has an associated NavigationDestination object, which defines the path to the screen and any additional arguments. For navigating to the ViewMidiScreen, a song ID is passed from the MidiRoomScreen via an onClick function created in the AppNavGraph and then used in the ViewMidiViewModel to retrieve the song from the app. 

### ![Room and Retrofit](https://github.com/andrea749/piano-companion/tree/main/app/src/main/java/com/andrea/pianocompanionroom/data)
This app uses Room to manage a local database. It currently supports searching by song name or artist, getting all the songs, getting one song by name or id, and includes the built-in functions: insert, update, and delete. It is used in the ![MidiRoomViewModel](https://github.com/andrea749/piano-companion/blob/main/app/src/main/java/com/andrea/pianocompanionroom/viewmodel/MidiRoomViewModel.kt) to insert new songs into the database, and the ![MidiUploadViewModel](https://github.com/andrea749/piano-companion/blob/main/app/src/main/java/com/andrea/pianocompanionroom/viewmodel/MidiUploadViewModel.kt) to display all saved songs and search through the database.  
This app also uses Retrofit to get album covers from ![MusicBrains](https://musicbrainz.org/) and ![Cover Art Archive](https://coverartarchive.org/). It uses the user-inputted song name and artist name to query MusicBrainz for all associated releases, then picks one and displays the album cover. 

### DI
Hilt is used to inject dependencies, and providers are defined in the AppModule.  

### ![BLE](https://github.com/andrea749/piano-companion/tree/main/app/src/main/java/com/andrea/pianocompanionroom/ble)
Currently a majority of the code sits in the BLE class, and is hardcoded for a specific device, service, and characteristic (my UUIDs are set in the arduino code). For the purposes of this project, all that needs to be done is connect to the specified device and send a ByteStream and occasionally some other bytes (the behavior of play/pause is TBD). Because of this, the main BLE API is scanAndConnnectToTarget and writeCharacteristic.  
