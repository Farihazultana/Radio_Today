package com.example.radiotoday.data.services


import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.radiotoday.data.models.SongList
import com.example.radiotoday.ui.fragments.PlayerFragment
import com.example.radiotoday.utils.AppUtils
import com.example.radiotoday.utils.NotificationController
import com.example.radiotoday.utils.NotificationUtils
import com.example.radiotoday.ui.interfaces.PlayAction
import com.example.radiotoday.utils.SharedPreferencesUtil


@OptIn(UnstableApi::class)
class MusicPlayerService : Service(), PlayAction {
    private val notificationReceiver: NotificationController = NotificationController()


    private lateinit var mediaSession: MediaSessionCompat

    private val binder = MusicPlayerBinder()

    private lateinit var player: ExoPlayer

    private var isPlaying: Boolean = false

    private var currentPosition : Long = 0L
    private var duration : Long = 0L

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()



        val filter = IntentFilter().apply {
            addAction("Pause")
            addAction("Next")
            addAction("Previous")
        }
        registerReceiver(notificationReceiver, filter, RECEIVER_NOT_EXPORTED)
        mediaSession = MediaSessionCompat(this, "MusicPlayerService")

        player = ExoPlayer.Builder(this).build()
        
        PlayerFragment.setOnPlayAction(this)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(1, NotificationUtils.createNotification(this, isPlaying, currentPosition, duration, SongList.getSongsList()),FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

        intent?.getStringExtra("action")?.let { action ->

            when (action) {
                "Previous" -> {
                    Toast.makeText(this, "Play Previous", Toast.LENGTH_SHORT).show()
                    previousMusic()
                }
                "Pause" -> {
                    Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show()
                    pauseMusic()
                }
                "Play" -> {
                    Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show()
                    playMusic()

                }
                "Next" -> {
                    Toast.makeText(this, "Play Next", Toast.LENGTH_SHORT).show()
                    nextMusic()
                }

                else -> {}
            }
        }

        val startPlayerStatus = SharedPreferencesUtil.getData(applicationContext, AppUtils.SWITCH, false)

        /*if (startPlayerStatus == true) {
            // Start playing music if the switch is on
            startForeground(
                1,
                NotificationUtils.createNotification(
                    this,
                    mediaSession,
                    isPlaying,
                    currentPosition,
                    duration,
                    mediaPlayerDataList
                ),
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )

            val liveUrls = listOf("http://stream.zeno.fm/8wv4d8g4344tv", "http://stream.zeno.fm/8wv4d8g4344tv")
            //initializePlayer()
            isPlaying = true
            playMusic()
        } else {
            // Stop playing music if the switch is off
            pauseMusic()
        }*/

        return START_NOT_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        releasePlayer()
    }

    override fun playMusic() {
        player.play()
        isPlaying = true
        notifyPlaybackStateChanged()
    }

    override fun pauseMusic() {
        player.pause()
        isPlaying = false
        notifyPlaybackStateChanged()
    }

    override fun previousMusic() {
        player.seekToPreviousMediaItem()
        notifyPlaybackStateChanged()
    }

    override fun nextMusic() {
        player.seekToNextMediaItem()
        notifyPlaybackStateChanged()
    }

    override fun isPlaying() : Boolean {
        return player.isPlaying
    }

    override fun shuffleMusic() {
        player.shuffleModeEnabled = true
    }

    override fun playerCurrentPosition() : Long {
        currentPosition = 0L
        if (player.playbackState == Player.STATE_READY) {
            currentPosition = player.currentPosition
        }
        return currentPosition
    }


    override fun playerDuration() : Long{
        duration = 0L
        if (player.playbackState == Player.STATE_READY) {
            duration = player.duration
        }
        return duration
    }

    override fun releasePlayer() {
        if (this::player.isInitialized) {
            player.release()
        }
    }

    override fun initializePlayer() {

        val songList = SongList.getSongsList()
        val position = SongList.getCurrentPosition()

        if (songList.isNotEmpty()) {

            val mediaItems = mutableListOf<MediaItem>()

            for (item in songList) {
                item.url?.let { url ->
                    val mediaItem = MediaItem.fromUri(url)
                    mediaItems.add(mediaItem)
                }
            }

            if (mediaItems.isNotEmpty()) {
                player.clearMediaItems()
                player.addMediaItems(mediaItems)
                player.prepare()
                player.seekTo(position, 0)
            } else {
                Log.i("MusicPlayerService", "initializePlayer: No media items found in the song list")
            }
        } else {
            Log.i("MusicPlayerService", "initializePlayer: SongList is empty!")
        }

    }

    override fun getPlayer(): ExoPlayer {
        return player
    }

    private fun notifyPlaybackStateChanged() {
        val intent = Intent("PlaybackState")
        intent.putExtra("isPlaying", isPlaying)
        sendBroadcast(intent)
    }


}
