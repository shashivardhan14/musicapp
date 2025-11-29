package com.example.music.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val albumId: Long,
    val data: String
): Parcelable {

}
