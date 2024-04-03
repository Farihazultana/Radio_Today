package com.example.radiotoday.data.services


import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.IInterface
import android.os.Looper
import android.os.Parcel
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.load.DataSource
import com.example.radiotoday.ui.fragments.SongsFragment
import com.example.radiotoday.utils.NotificationController
import com.example.radiotoday.utils.NotificationUtils
import com.example.radiotoday.utils.PlayAction
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import java.io.FileDescriptor


@OptIn(UnstableApi::class)
class MusicPlayerService : Service(), IBinder, PlayAction {
    private val notificationReceiver: NotificationController = NotificationController()
    private val CHANNEL_ID = "Music Service Channel ID"
    private val UPDATE_INTERVAL_MILLIS = 1000
    private lateinit var notificationManager: NotificationManager
    private lateinit var updateProgressRunnable: Runnable
    private lateinit var updateProgressHandler: Handler

    private lateinit var mediaSession: MediaSessionCompat

    private val binder = MusicPlayerBinder()

    private lateinit var player: ExoPlayer

    private var isPlaying: Boolean = false

    private var currentPosition : Long = 0L
    private var duration : Long = 0L

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onCreate() {
        super.onCreate()

        NotificationUtils.createNotificationChannel(this)

        val filter = IntentFilter().apply {
            addAction("Pause")
            addAction("Next")
            addAction("Previous")
        }
        registerReceiver(notificationReceiver, filter)
        mediaSession = MediaSessionCompat(this, "MusicPlayerService")

        updateProgressHandler = Handler(Looper.getMainLooper())
        updateProgressRunnable = object : Runnable {
            override fun run() {
                NotificationUtils.updateNotification(this@MusicPlayerService, isPlaying(), mediaSession, currentPosition, duration)
                updateProgressHandler.postDelayed(this, UPDATE_INTERVAL_MILLIS.toLong())
            }
        }
        updateProgressHandler.postDelayed(updateProgressRunnable, UPDATE_INTERVAL_MILLIS.toLong())

        //initializePlayer()
        player = ExoPlayer.Builder(this).build()
        SongsFragment.setOnPlayAction(this)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, NotificationUtils.createNotification(this, mediaSession, isPlaying, currentPosition, duration),FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

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

        return START_NOT_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        releasePlayer()
        updateProgressHandler.removeCallbacks(updateProgressRunnable)
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
        //startForeground(1, createNotification(isPlaying))
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

    override fun trackSelector() {
        /*player.addListener(
            object : Player.Listener {
                override fun onTracksChanged(tracks: Tracks) {
                    val audioList = mutableListOf<String>()
                    var language: String = ""
                    for (trackGroup in tracks.groups) {
                        val trackType = trackGroup.type

                        if (trackType == C.TRACK_TYPE_AUDIO) {
                            for (i in 0 until trackGroup.length) {
                                val trackFormat = trackGroup.getTrackFormat(i)
                                language =
                                    trackFormat.language ?: "und"

                                audioList.add("${audioList.size + 1}. " + Locale(language).displayLanguage)
                            }
                        }
                    }

                    if (audioList.isEmpty()) {
                        // No audio tracks available
                        return
                    }

                    if (audioList[0].contains("null")) {
                        audioList[0] = "1. Default Tracks"
                    }

                    val tempTracks = audioList.toTypedArray()

                    val audioDialog = MaterialAlertDialogBuilder(
                        this@MusicPlayerService,
                        R.style.Base_Theme_ExoPlayer
                    )
                        .setTitle("Select Language")
                        .setOnCancelListener { player.play() }
                        .setPositiveButton("Off Audio") { self, _ ->
                            // Handle turning off audio if needed
                            player.trackSelectionParameters =
                                player.trackSelectionParameters
                                    .buildUpon()
                                    .setMaxVideoSizeSd()
                                    .build()
                            self.dismiss()
                        }
                        .setItems(tempTracks) { _, position ->
                            // Handle selecting audio track
                            Toast.makeText(
                                this@MusicPlayerService,
                                audioList[position] + " Selected",
                                Toast.LENGTH_SHORT
                            ).show()

                            val selectedLanguage = Locale(language).language
                            player.trackSelectionParameters =
                                player.trackSelectionParameters
                                    .buildUpon()
                                    .setMaxVideoSizeSd()
                                    .setPreferredAudioLanguage(selectedLanguage)
                                    .build()
                        }
                        .create()

                    audioDialog.show()
                    audioDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)
                    audioDialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                }
            }
        )*/
    }

    override fun initializePlayer(songsUrls : ArrayList<String>) {
        player = ExoPlayer.Builder(this).build()

        player.clearMediaItems()

        for (url in songsUrls){
            val mediaItem = MediaItem.fromUri(url)
            player.addMediaItem(mediaItem)
        }


        player.prepare()

    }

    override fun getPlayer(): ExoPlayer {
        return player
    }

    private fun notifyPlaybackStateChanged() {
        val intent = Intent("PlaybackState")
        intent.putExtra("isPlaying", isPlaying)
        sendBroadcast(intent)
        NotificationUtils.updateNotification(this, isPlaying(), mediaSession, currentPosition, duration)

    }


}
