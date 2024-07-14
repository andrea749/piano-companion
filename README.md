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
<img src="/uploadFlow.png" width="110" height="122"><img src="/MidiRoomScreen.png" width="224" height="498">
Here, all the songs stored in the local database are displayed in clickable cards. There is a search bar above the list and it narrows down song cards as the user starts typing their query.
When the user selects a song, the song ID is sent to ViewMidiScreen via the NavigationDestination.

### ViewMidiScreen
<img src="/ViewMidiScreen.png" width="224" height="498">
A misnomer, this doesn't display a MIDI file but rather holds controls for streaming to the arudino. Currently, an animated countdown displays and then the song is sent via bluetooth. The play/pause buttons, along with rewind/ff, are not operational currently.


Additional implementation details:
- Compose navigation
- BLE
- Room




