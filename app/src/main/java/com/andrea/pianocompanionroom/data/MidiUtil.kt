package com.andrea.pianocompanionroom.data

import android.util.Log
import com.leff.midi.MidiFile
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import kotlin.math.ceil

// TODO: request MTU and set byte array size to that
private const val BYTE_ARRAY_SIZE = 32
fun parseMidiFile(input: InputStream): Array<ByteArray> {
    val midiFile = MidiFile(input)
    val noteEvents: MutableList<List<Number>> = mutableListOf()
    var tempo = 0f

    for (track in midiFile.tracks) {
        val iterator: Iterator<MidiEvent> = track.events.iterator()
        while (iterator.hasNext()) {
            when (val event = iterator.next()) {
                is NoteOff -> {
                    noteEvents.add(listOf(event.tick, event.noteValue, event.velocity))
                }

                is NoteOn -> {
                    noteEvents.add(listOf(event.tick, event.noteValue, event.velocity))
                }

                is Tempo -> {
                    tempo = event.bpm
                }
            }
        }
    }
    val song = Song(tempo = tempo, numOfEvents = noteEvents.size, notes = noteEvents.toList())
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