package com.example.music.ui.theme

import android.content.ContentUris
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.music.data.Song
import com.example.music.R


@Composable
fun SongList(
    songs: List<Song>,
    onSongClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(songs) { index, song ->
            SongListItem(
                song = song,
                onClick = { onSongClick(index) }
            )

        }

    }
}

@Composable
fun SongListItem(song: Song, onClick: () -> Unit) {
    val albumArtUri = ContentUris.withAppendedId(
        Uri.parse("content://media/external/audio/albumart"),
        song.albumId
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = albumArtUri,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Color(0 * 33000000)
                ), contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.musical_note),
            placeholder = painterResource(R.drawable.musical_note)

        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = song.title.orEmpty(),
                color = Color.White,
                fontWeight = FontWeight.Bold, fontSize = 16.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist.orEmpty(),
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )

        }


    }

}