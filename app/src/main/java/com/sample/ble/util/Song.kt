package com.sample.ble.util

import android.content.Context
import android.util.Log
import com.leff.midi.MidiFile
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
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


private const val BYTE_ARRAY_SIZE = 20
@Serializable
data class Song(val tempo: Float, val notes: MutableList<MutableList<@Serializable(with = NumberSerializer::class)Number>>, val numOfEvents: Int = notes.size)

object NumberSerializer : KSerializer<Number> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Number", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Number {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Number) {
        encoder.encodeString(value.toString())
    }

}


fun parseMidiFile(context: Context, fileName: String = "no name.mid"): Array<ByteArray> {
    val input = context.assets.open(fileName)
    val midiFile = MidiFile(input)
    val noteEvents: MutableList<MutableList<Number>> = mutableListOf()
    var tempo = 0f

    for (track in midiFile.tracks) {
        val iterator: Iterator<MidiEvent> = track.events.iterator()
        while (iterator.hasNext()) {
            // TODO: create generic Note class
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
    val song = Song(tempo, noteEvents, noteEvents.size)
    val byteArray = Json.encodeToString(song).encodeToByteArray()
    val allByteArrays = splitByteArray(byteArray)
    Log.d("andrea", "returning byte arrays")
    return allByteArrays
}

fun splitByteArray(byteArray: ByteArray): Array<ByteArray> {
    val chunkSize = BYTE_ARRAY_SIZE
    val numChunks = ceil(byteArray.size.toDouble()/chunkSize).toInt()
    val allByteArrays: Array<ByteArray> = Array(numChunks) { ByteArray(20) }
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





