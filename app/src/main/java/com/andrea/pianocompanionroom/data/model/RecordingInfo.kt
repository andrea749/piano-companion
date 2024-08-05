package com.andrea.pianocompanionroom.data.model

import com.google.gson.annotations.SerializedName

data class RecordingInfo(
    val id: String,
    val title: String,
    @SerializedName("artist-credit")
    val artistCredit: List<ArtistCredit>,
    )

data class ArtistCredit(
    val artist: ArtistInfo,
)
