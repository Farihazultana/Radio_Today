package com.example.radiotoday.ui.interfaces

import androidx.media3.exoplayer.ExoPlayer
import com.example.radiotoday.data.models.SubContent

interface PlayAction {

    fun getPlayer() : ExoPlayer

    fun initializePlayer()

    fun getTitle() : String

    fun getArtist() : String

    fun playMusic()

    fun pauseMusic()

    fun previousMusic()

    fun nextMusic()

    fun isPlaying() : Boolean

    fun shuffleMusic()

    fun playerCurrentPosition() : Long

    fun playerDuration() : Long

    fun releasePlayer()

}