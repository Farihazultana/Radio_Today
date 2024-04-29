package com.example.radiotoday.data.services


import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
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
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.ui.fragments.SongsFragment
import com.example.radiotoday.utils.AppUtils
import com.example.radiotoday.utils.NotificationController
import com.example.radiotoday.utils.NotificationUtils
import com.example.radiotoday.utils.PlayAction
import com.example.radiotoday.utils.SharedPreferencesUtil
import java.io.FileDescriptor


@OptIn(UnstableApi::class)
class MusicPlayerService : Service(), IBinder, PlayAction{
    private val notificationReceiver: NotificationController = NotificationController()

    companion object{
        var mediaPlayerDataList = listOf<SubContent>()
    }


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

        NotificationUtils.createNotificationChannel(this)

        val filter = IntentFilter().apply {
            addAction("Pause")
            addAction("Next")
            addAction("Previous")
        }
        registerReceiver(notificationReceiver, filter, RECEIVER_NOT_EXPORTED)
        mediaSession = MediaSessionCompat(this, "MusicPlayerService")

        player = ExoPlayer.Builder(this).build()
        
        SongsFragment.setOnPlayAction(this)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, NotificationUtils.createNotification(this, mediaSession, isPlaying, currentPosition, duration, SongList.getSongsList()),FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

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

    override fun getInterfaceDescriptor(): String? {
        return null
    }

    override fun pingBinder(): Boolean {
        return false
    }

    override fun isBinderAlive(): Boolean {
        return false
    }

    override fun queryLocalInterface(descriptor: String): IInterface? {
        return null
    }

    override fun dump(fd: FileDescriptor, args: Array<out String>?) {

    }

    override fun dumpAsync(fd: FileDescriptor, args: Array<out String>?) {

    }

    override fun transact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        return false
    }

    override fun linkToDeath(recipient: IBinder.DeathRecipient, flags: Int) {

    }

    override fun unlinkToDeath(recipient: IBinder.DeathRecipient, flags: Int): Boolean {
        return false
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

    override fun initializePlayer(songList: ArrayList<SubContent>, position : Int) {

        if (songList.isNotEmpty()){
            player.clearMediaItems()

            val mediaItem = songList[position].url?.let { MediaItem.fromUri(it) }
            if (mediaItem != null) {
                player.addMediaItem(mediaItem)
            }
            player.prepare()
        }else{
            Log.i("DPlayer", "initializePlayer: SongList is empty!")
        }
        
            /*for (item in songList){
                val mediaItem = item.url?.let { MediaItem.fromUri(it) }
                if (mediaItem != null) {
                    player.addMediaItem(mediaItem)
                }
            }*/

        

    }

    override fun getPlayer(): ExoPlayer {
        return player
    }

    private fun notifyPlaybackStateChanged() {
        val intent = Intent("PlaybackState")
        intent.putExtra("isPlaying", isPlaying)
        sendBroadcast(intent)
    }

    /*override fun onSongSelected(song: SubContent) {
        releasePlayer()
        Log.i("DPlayer", "onSongSelected: ${song.url}")
        song.url?.let { initializePlayer(it) }
    }*/


}
