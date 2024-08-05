# Room  

#### Usage  
* to ![insert](https://github.com/andrea749/piano-companion/blob/4a33a995cdfb703b24f725acd95ee703d07932c5/app/src/main/java/com/andrea/pianocompanionroom/viewmodel/MidiUploadViewModel.kt#L27) a song, inject an instance of SongsRepository and use `insertSong()`
* to ![retrieve](https://github.com/andrea749/piano-companion/blob/4a33a995cdfb703b24f725acd95ee703d07932c5/app/src/main/java/com/andrea/pianocompanionroom/viewmodel/MidiRoomViewModel.kt#L24) songs, run `getAllSongs()` inside a coroutine scope, and pass the result via a UI State
* if you want to retrieve a song and ![use](https://github.com/andrea749/piano-companion/blob/4a33a995cdfb703b24f725acd95ee703d07932c5/app/src/main/java/com/andrea/pianocompanionroom/viewmodel/ViewMidiViewModel.kt#L50) it in a StateFlow, first make a Mutable UI State and merge it with the flow from `getSongStream()`. Now, if we update the UI State, it'll trigger a recomposition.

#### SongInventoryDatabase  
* defines what type of object is in the table, what (if any) TypeConverters are needed (example ![here](https://github.com/andrea749/piano-companion/blob/main/app/src/main/java/com/andrea/pianocompanionroom/data/MidiEventDataConverter.kt)), and what migration strategy should be used. I used destructiveMigration but will probably change this later so as not to repopulate the table every time I want to change the schema.
  
#### OfflineSongsRepository  
* implements the functions in SongsRepository, calls on the SongDao
* no need for a network version since the user uploads files
  
#### SongDao  
* has all the queries to the offline database. Some are provided by Room, but all the GET type of functions need custom queries.
  
#### Song  
* has a `primaryKey` (required by Room) and some fake data. `Song.notes` is of type `List<List<Number>>` since the "note" might contain ints (for pitch or velocity) or floats (for tick). `Number` is not a type that Room can automatically store in the db, so `MidiEventConverter` is required to convert this between `List<List<Number>>` <-> `String`
  
#### MidiUtil    
* parseMidiFile converts a MIDI file into a Song (made up mostly of strings), making it easy to store in the db.  


# Retrofit
##### Retrofit is used to make network calls to a remote database called MusicBrainz, which provides information about songs, artists, etc.  

#### MusicDbRepository
* interface between the ViewModel and the network API
* defines the functions we'll perform on the database (get artist ID, get song ID, get release ID)
* actual calls are created by Retrofit using functions defined in MusicDbApiService
* information from this repository is used in MidiUploadViewModel to generate a link to an album cover 