package com.example.music.ui.theme



import android.content.ContentUris
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.music.data.Song
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.example.music.R

/**
 * Composable function for the music player screen.
 *
 * @param songList The list of songs to be played.
 * @param initialIndex The starting index of the song in the songList.
 * @param onBack A lambda function to be invoked when the back button is pressed.
 */
@Composable
fun PlayerScreen(
    songList: List<Song>,
    initialIndex: Int = 0,
    onBack: () -> Unit
) {
    // Get the current context
    val context = LocalContext.current
    // Remember an ExoPlayer instance for media playback
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    // State for the current song's index, saved across configuration changes
    var currentIndex by rememberSaveable { mutableStateOf(initialIndex) }
    // State for shuffle mode, saved across configuration changes
    var isShufflie by rememberSaveable { mutableStateOf(false) }
    // State for repeat mode, saved across configuration changes
    var isRepeat by rememberSaveable { mutableStateOf(false) }
    // State to track if the player is currently playing, saved across configuration changes
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    // State for the elapsed time of the current song
    var elapsed by remember { mutableStateOf(0L) }
    // State for the total duration of the current song
    var duration by remember { mutableStateOf(0L) }
    // State to hold the shuffled list of songs
    var shuffledList by remember { mutableStateOf(songList) }


    // Remember a generated waveform for the current song
    val waveForm = remember { getWaveForm() }
    // State for the progress of the waveform visualization
    var waveFormProgress by remember { mutableStateOf(0f) }

    // Effect that runs when currentIndex or isShufflie changes
    LaunchedEffect(key1 = currentIndex, key2 = isShufflie) {
        // Determine which list to use (shuffled or original)
        val list = if (isShufflie) shuffledList else songList
        // Get the current song, return if not found
        val song = list.getOrNull(currentIndex) ?: return@LaunchedEffect
        // Set the media item for ExoPlayer and prepare it for playback
        exoPlayer.setMediaItem(MediaItem.fromUri(song.data))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    // Effect for managing ExoPlayer's lifecycle
    DisposableEffect(key1 = exoPlayer) {
        // Create a player listener to respond to player state changes
        val listener = object : Player.Listener {
            // Update isPlaying state when playback starts or stops
            override fun onIsPlayingChanged(isPlay: Boolean) {
                isPlaying = isPlay
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                // When the player is ready, get the song duration
                if (playbackState == Player.STATE_READY) {
                    duration = exoPlayer.duration
                }
                // When the song ends, move to the next one
                if (playbackState == Player.STATE_ENDED) {
                    currentIndex =
                        (currentIndex + 1) % (if (isShufflie) shuffledList.size else songList.size)
                }
            }
        }
        // Add the listener to ExoPlayer
        exoPlayer.addListener(listener)
        // Cleanup function that runs when the composable leaves the composition
        onDispose {
            // Release the ExoPlayer resources to avoid memory leaks
            exoPlayer.release()
        }
    }

    // Effect that runs continuously while the song is playing
    LaunchedEffect(key1 = isPlaying) {
        while (isPlaying) {
            // Update elapsed time and waveform progress
            elapsed = exoPlayer.currentPosition
            waveFormProgress = if (duration > 0) elapsed.toFloat() / duration else 0f
            delay(500)
        }
    }
    val song = (if (isShufflie) shuffledList else songList).getOrNull(currentIndex)
    Box(
        // Main container with a gradient background
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xff191c1f), Color(0xff2c2c38))))
    ) {
        // This block executes if the current song is not null
        song?.let {
            // Construct the URI for the album art
            val albumUri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"), it.albumId
            )
            // Display the album art as a blurred background
            AsyncImage(
                model = ImageRequest.Builder(context).data(albumUri).build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(18.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.40f,
                error = painterResource(R.drawable.musical_note),
                placeholder = painterResource(R.drawable.musical_note)
            )

            // Top bar with back and favorite buttons
            Row(Modifier.padding(horizontal = 16.dp, vertical = 48.dp)) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0x30ffffff), shape = CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0x30ffffff), shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.White
                    )

                }
            }

        }
        Column(
            modifier = Modifier
                // Center content at the top of the screen
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = song?.let {
                    ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        it.albumId
                    )
                }
                    ?: R.drawable.musical_note,
                contentDescription = null,
                modifier = Modifier
                    .size(320.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0x30ffffff), shape = CircleShape
                    ),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.musical_note),
                placeholder = painterResource(R.drawable.musical_note)
            )

            // Display song title
            Text(
                text = song?.title.orEmpty(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(top = 32.dp)

            )

            // Display artist name
            Text(
                text = song?.artist.orEmpty(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)

            )


        }
        Column(
            modifier = Modifier
                // Align this column to the center, with top padding
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .padding(top = 320.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom composable to display the song's waveform and progress
            WaveFormBar(
                values = waveForm,
                progress = waveFormProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) { percent ->
                // Seek to a new position in the song when the user interacts with the waveform
                val seek = (percent * duration).toLong()
                exoPlayer.seekTo(seek)
                elapsed = seek
                waveFormProgress = percent
            }

            // Row for displaying elapsed and total duration
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    // Format and display the elapsed time
                    text = formatTime(seconds = (elapsed / 1000).toInt()),
                    color = Color.White,
                    fontSize = 13.sp
                )
                Text(
                    // Format and display the total duration
                    text = formatTime(seconds = (duration / 1000).toInt()),
                    color = Color.White,
                    fontSize = 13.sp

                )
            }
        }

        // Row containing the player control buttons (repeat, previous, play/pause, next, shuffle)
        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 54.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Repeat button
            IconButton(onClick = {
                isRepeat = !isRepeat
                exoPlayer.repeatMode =
                    if (isRepeat) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
            }) {
                Icon(
                    painterResource(R.drawable.outline_repeat_one),
                    contentDescription = null,
                    tint = if (isRepeat) Color(0xff9c27b0) else Color.White
                )
            }

            // Skip-previous button
            IconButton(onClick = {
                val list = if (isShufflie) shuffledList else songList
                currentIndex = if (currentIndex - 1 < 0) list.size - 1 else currentIndex - 1
            }) {
                Icon(
                    painterResource(R.drawable.outline_skip_previous_one),
                    contentDescription = null,
                    tint = Color.White
                )
            }

            // Play or pause button
            IconButton(
                onClick = {
                    if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                }, modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Icon(
                    painterResource(if (isPlaying) R.drawable.outline_pause else R.drawable.baseline_play_arrow),
                    contentDescription = null,
                    tint = Color(0xff1a1a1a)
                )
            }

            // Skip-next button
            IconButton(onClick = {
                val list = if (isShufflie) shuffledList else songList
                currentIndex = (currentIndex + 1) % list.size
            }) {
                Icon(
                    painterResource(R.drawable.outline_skip_next_one),
                    contentDescription = null,
                    tint = if (isRepeat) Color(0xff9c27b0) else Color.White
                )
            }

            // Shuffle button
            IconButton(onClick = {
                isShufflie = !isShufflie
                // Shuffle the list if shuffle is enabled, otherwise revert to original order
                shuffledList = if (isShufflie) songList.shuffled() else songList

            }) {
                Icon(
                    painterResource(R.drawable.outline_shuffle),
                    contentDescription = null,
                    tint = if (isRepeat) Color(0xff9c27b0) else Color.White
                )
            }
        }

    }

}


/**
 * Generates a dummy array of integers to represent a song's waveform.
 * @return An IntArray with 50 random values.
 */
fun getWaveForm(): IntArray {
    val random = Random(System.currentTimeMillis())
    return IntArray(50) { 5 + random.nextInt(50) }
}

/**
 * Formats a given time in seconds into a "MM:SS" string format.
 * @param seconds The total seconds to format.
 * @return A formatted string (e.g., "03:45").
 */
fun formatTime(seconds: Int): String = String.format("%02d:%02d", seconds / 60, seconds % 60)

