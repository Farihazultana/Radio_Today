package com.example.radiotoday.utils

import androidx.media3.exoplayer.ExoPlayer
import com.example.radiotoday.data.models.MediaPlayerData

interface PlayAction {

    fun getPlayer() : ExoPlayer
    fun initializePlayer(mediaPlayerDataList: List<MediaPlayerData>)
    fun playMusic()

    fun pauseMusic()

    fun previousMusic()

    fun nextMusic()

    fun isPlaying() : Boolean

    fun shuffleMusic()

    fun playerCurrentPosition() : Long

    fun playerDuration() : Long

    fun releasePlayer()

    fun trackSelector()

}