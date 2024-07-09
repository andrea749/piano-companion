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
    Song(name = "Feminomenon", artist = "Chappell Roan", duration = "3:40"),
    Song(name = "Francesca", artist = "Hozier", duration = "4:31"),
    Song(name = "Edge of the Earth", artist = "The Beaches", duration = "2:41"),
    Song(name = "ghj", artist = "kjsdf"),
    Song(name = "ghj", artist = "kjsdf"),
    Song(name = "ghj", artist = "kjsdf"),
    Song(name = "ghj", artist = "kjsdf"),
    Song(name = "ghj", artist = "kjsdf"),
    Song(name = "ghj", artist = "kjsdf"),
    Song(name = "ghj", artist = "kjsdf"),
    Song(name = "ghj", artist = "kjsdf"),
    Song(name = "sdf", artist = "xcgv"),
    Song(name = "sdf", artist = "xcgv"),
    Song(name = "sdf", artist = "xcgv"),
    Song(name = "sdf", artist = "xcgv"),
    Song(name = "sdf", artist = "xcgv"),
    Song(name = "sdf", artist = "xcgv"),
    Song(name = "sdf", artist = "xcgv"),
    Song(name = "sdf", artist = "xcgv"),
    Song(name = "bmn", artist = "xvcv"),
    )