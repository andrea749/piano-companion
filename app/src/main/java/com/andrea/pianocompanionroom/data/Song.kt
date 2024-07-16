package com.andrea.pianocompanionroom.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import androidx.room.Entity
import androidx.room.PrimaryKey


@Serializable
@Entity(tableName = "Songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val artist: String = "",
    val duration: String = "", // purely for display purposes for now
    val tempo: Float = 0F,
    val numOfEvents: Int = 0,
    val notes: List<List<@Serializable(with = NumberSerializer::class)Number>> = listOf()
)



object NumberSerializer : KSerializer<Number> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Number", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Number {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Number) {
        encoder.encodeString(value.toString())
    }
}

val fakeSongData = listOf(
    Song(name = "Super Graphic Ultra Modern Girl", artist = "Chappell Roan", duration = "3:04"),
    Song(name = "Femininomenon", artist = "Chappell Roan", duration = "3:40"),
    Song(name = "Francesca", artist = "Hozier", duration = "4:31"),
    Song(name = "Edge of the Earth", artist = "The Beaches", duration = "2:41"),
    Song(name = "Creep", artist = "Radiohead", duration = "3:59"),
    Song(name = "Fade Into You", artist = "Mazzy Star", duration = "4:56"),
    Song(name = "Just A Girl", artist = "No Doubt", duration = "3:59"),
    Song(name = "Hand In My Pocket", artist = "Alanis Morissette", duration = "3:43"),
    Song(name = "Murder On The Dancefloor", artist = "Sophie Ellis-Bextor", duration = "3:51"),
    Song(name = "Espresso", artist = "Sabrina Carpenter", duration = "3:03"),
    Song(name = "Not My Fault", artist = "Renee Rapp & Megan Thee Stallion", duration = "3:59"),
    Song(name = "Green Light", artist = "Lorde", duration = "3:11"),
    Song(name = "These Arms Of Mine", artist = "Otis Redding", duration = "3:10"),
    Song(name = "Hard Times", artist = "Paramore", duration = "3:03"),
    Song(name = "Como La Flor", artist = "Selena", duration = "3:07"),
    Song(name = "Limon y Sal", artist = "Julieta Venegas", duration = "3:26"),
    )
