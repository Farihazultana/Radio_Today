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


        /*mediaPlayerDataList.add(MediaPlayerData(
            title = "Title 1",
            description = "Description 1",
            img = "https://img.freepik.com/free-photo/painting-mountain-lake-with-mountain-background_188544-9126.jpg",
            url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
        ))
        mediaPlayerDataList.add(MediaPlayerData(
            title = "Title 2",
            description = "Description 2",
            img = "https://img.freepik.com/free-photo/painting-mountain-lake-with-mountain-background_188544-9126.jpg",
            url = "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3"
        ))
        mediaPlayerDataList.add(MediaPlayerData(
            title = "Title 3",
            description = "Description 1",
            img = "https://www.kasandbox.org/programming-images/avatars/old-spice-man.png",
            url = "https://github.com/rafaelreis-hotmart/Audio-Sample-files/raw/master/sample.mp3"
        ))
        mediaPlayerDataList.add(MediaPlayerData(
            title = "Title 4",
            description = "Description 2",
            img = "https://www.kasandbox.org/programming-images/avatars/mr-pants-green.png",
            url = "https://github.com/SergLam/Audio-Sample-files/raw/master/sample.m4a"
        ))
        mediaPlayerDataList.add(MediaPlayerData(
            title = "Title 5",
            description = "Description 1",
            img = "https://www.kasandbox.org/programming-images/avatars/marcimus-purple.png",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        ))
        mediaPlayerDataList.add(MediaPlayerData(
            title = "Title 6",
            description = "Description 2",
            img = "https://www.kasandbox.org/programming-images/avatars/marcimus-orange.png",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        ))
        mediaPlayerDataList.add(MediaPlayerData(
            title = "Title 7",
            description = "Description 1",
            img = "https://www.kasandbox.org/programming-images/avatars/mr-pants-purple.png",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
        ))
        mediaPlayerDataList.add(MediaPlayerData(
            title = "Title 8",
            description = "Description 2",
            img = "https://www.kasandbox.org/programming-images/avatars/mr-pants-green.png",
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        ))*/

        //initializePlayer(mediaPlayerDataList)
        player = ExoPlayer.Builder(this).build()
        
        SongsFragment.setOnPlayAction(this)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, NotificationUtils.createNotification(this, mediaSession, isPlaying, currentPosition, duration, mediaPlayerDataList),FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

        if(player != null){
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

                        /*val selectedSong =  intent.getParcelableArrayListExtra<Parcelable>("selectedSong")?.filterIsInstance<SubContent>()
                        playSelectyedSong(selectedSong)*/

                    }
                    "Next" -> {
                        Toast.makeText(this, "Play Next", Toast.LENGTH_SHORT).show()
                        nextMusic()
                    }

                    else -> {}
                }
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
