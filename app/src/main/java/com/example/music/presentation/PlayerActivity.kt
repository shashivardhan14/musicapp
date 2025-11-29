package com.example.music.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.music.R
import com.example.music.data.Song
import com.example.music.ui.theme.PlayerScreen

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mySongList = intent.getParcelableArrayListExtra("songList")?:emptyList<Song>()
        val initialIndex = intent.getIntExtra("position",0)
        setContent {
            PlayerScreen(songList = mySongList, initialIndex = initialIndex, onBack = {finish()}
            )
        }
    }
}