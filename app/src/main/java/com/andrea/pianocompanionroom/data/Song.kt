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
import androidx.room.TypeConverters


@Serializable
@Entity(tableName = "Songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val artist: String = "",
    val tempo: Float,
    val numOfEvents: Int,
    val notes: List<List<@Serializable(with = NumberSerializer::class)Number>>
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