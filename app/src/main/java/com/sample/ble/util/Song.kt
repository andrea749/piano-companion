package com.sample.ble.util

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.core.content.res.TypedArrayUtils.getString
import com.leff.midi.MidiFile
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.util.Arrays
import kotlin.math.ceil

// TODO: request MTU and set byte array size to that
private const val BYTE_ARRAY_SIZE = 32
@Serializable
data class Song(val tempo: Float, val numOfEvents: Int, val notes: MutableList<MutableList<@Serializable(with = NumberSerializer::class)Number>>)

object NumberSerializer : KSerializer<Number> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Number", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Number {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Number) {
        encoder.encodeString(value.toString())
    }

}

// was moving things to util, is prickly. trying to figure out if getSystem will give me the correct resources, or if it will look at phone resources. matters bc i have some strings in my res file.
// might need to pass around the correct context instead
fun parseMidiFile(fileName: String = "no name.mid"): Array<ByteArray> {
//    val input = context.assets.open(fileName)
    val input = Resources.getSystem().assets.open(fileName)
    val midiFile = MidiFile(input)
    val noteEvents: MutableList<MutableList<Number>> = mutableListOf()
    var tempo = 0f

    for (track in midiFile.tracks) {
        val iterator: Iterator<MidiEvent> = track.events.iterator()
        while (iterator.hasNext()) {
            when (val event = iterator.next()) {
                is NoteOff -> {
                    noteEvents.add(mutableListOf(event.tick, event.noteValue, event.velocity))
                }

                is NoteOn -> {
                    noteEvents.add(mutableListOf(event.tick, event.noteValue, event.velocity))
                }

                is Tempo -> {
                    tempo = event.bpm
                }
            }
        }
    }
    val song = Song(tempo, noteEvents.size, noteEvents)
    Log.d("andrea", "song: ${Json.encodeToString(song)}")
    val byteArray = Json.encodeToString(song).encodeToByteArray()
    val allByteArrays = splitByteArray(byteArray)
    Log.d("andrea", "returning byte arrays")
    return allByteArrays
}

fun splitByteArray(byteArray: ByteArray): Array<ByteArray> {
    val chunkSize = BYTE_ARRAY_SIZE
    val numChunks = ceil(byteArray.size.toDouble()/chunkSize).toInt()
    val allByteArrays: Array<ByteArray> = Array(numChunks) { ByteArray(BYTE_ARRAY_SIZE) }
    var start = 0
    var end = chunkSize
    for (i in 0 until numChunks) {
        if (end > byteArray.size) {
            end = byteArray.size
        }
        allByteArrays[i] = byteArray.copyOfRange(start, end)
        start = end
        end = start + chunkSize
    }
    return allByteArrays
}





