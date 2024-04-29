package com.example.radiotoday.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.bumptech.glide.Glide
import com.example.radiotoday.R
import com.example.radiotoday.data.models.SubContent
import com.example.radiotoday.ui.activities.MainActivity
import com.example.radiotoday.ui.fragments.PlayerFragment.Companion.onPlayAction

object NotificationUtils {
    private val CHANNEL_ID = "Music Service Channel ID"
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    fun createNotification(
        context: Context,
        mediaSession: MediaSessionCompat,
        isPlaying: Boolean,
        currentPosition: Long,
        duration: Long,
        songList: List<SubContent>
    ): Notification {
        if (songList.isEmpty()) {

            return createDefaultNotification(context)
        }

        val currentMediaItemIndex = onPlayAction.getPlayer().currentMediaItemIndex
        val currentMediaItem = songList[currentMediaItemIndex]

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("fragment", "songs")
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)


        val mediaStyle = MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(0, 1, 2, 3)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val playbackSpeed = if (isPlaying) 1F else 0F
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        onPlayAction.playerDuration()
                    )
                    .build()
            )

            mediaSession.setPlaybackState(
                PlaybackStateCompat.Builder().setState(
                    if (isPlaying) {
                        PlaybackStateCompat.STATE_PLAYING
                    } else {
                        PlaybackStateCompat.STATE_PAUSED
                    }, onPlayAction.playerCurrentPosition(), playbackSpeed
                )
                    .setActions(
                        PlaybackStateCompat.ACTION_SEEK_TO or
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                                if (isPlaying) {
                                    PlaybackStateCompat.ACTION_PAUSE
                                } else {
                                    PlaybackStateCompat.ACTION_PLAY
                                } or
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                    .build()
            )

            val callback = object: MediaSessionCompat.Callback() {
                override fun onPlay() {
                    onPlayAction.playMusic()
                }

                override fun onPause() {
                    onPlayAction.pauseMusic()
                }

                override fun onSkipToPrevious() {
                    onPlayAction.previousMusic()
                }

                override fun onSkipToNext() {
                    onPlayAction.nextMusic()
                }

            }

            mediaSession.setCallback(callback)

        }else{
            val playbackSpeed = if (isPlaying) 1F else 0F
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        onPlayAction.playerDuration()
                    )
                    .build()
            )

            mediaSession.setPlaybackState(
                PlaybackStateCompat.Builder().setState(
                    if (isPlaying) {
                        PlaybackStateCompat.STATE_PLAYING
                    } else {
                        PlaybackStateCompat.STATE_PAUSED
                    }, onPlayAction.playerCurrentPosition(), playbackSpeed
                )
                    .setActions(
                        PlaybackStateCompat.ACTION_SEEK_TO or
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                                if (isPlaying) {
                                    PlaybackStateCompat.ACTION_PAUSE
                                } else {
                                    PlaybackStateCompat.ACTION_PLAY
                                } or
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                    .build()
            )

            val callback = object: MediaSessionCompat.Callback() {
                override fun onPlay() {
                    onPlayAction.playMusic()
                }

                override fun onPause() {
                    onPlayAction.pauseMusic()
                }

                override fun onSkipToPrevious() {
                    onPlayAction.previousMusic()
                }

                override fun onSkipToNext() {
                    onPlayAction.nextMusic()
                }

            }

            mediaSession.setCallback(callback)
        }


        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val playPause = if (isPlaying) "Pause" else "Play"

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(currentMediaItem.title)
            .setContentText("Playing Music")
            .setSmallIcon(R.drawable.ic_music)
            .setLargeIcon(currentMediaItem.image?.let { getBitmapFromUrl(context, it) } ?: BitmapFactory.decodeResource(context.resources, R.drawable.album_cover))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(mediaStyle)
            .setSilent(true)
            .setProgress(duration.toInt(), currentPosition.toInt(), false)
            .addAction(R.drawable.ic_previous, "Previous", getPendingIntent(context, "Previous"))
            .addAction(playPauseIcon, playPause, getPendingIntent(context, playPause))
            .addAction(R.drawable.ic_next, "Next", getPendingIntent(context, "Next"))
            .setSound(Uri.EMPTY)


        return notificationBuilder.build()
    }

    private fun createDefaultNotification(context: Context): Notification {

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Default Title")
            .setContentText("No media items available")
            .setSmallIcon(R.drawable.ic_music)
            .build()
    }

    private fun getPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, NotificationController::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    fun updateNotification(
        context: Context, isPlaying: Boolean, mediaSession: MediaSessionCompat,
        currentPosition: Long, duration: Long, songList: List<SubContent>
    ) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, createNotification(context,mediaSession, isPlaying, currentPosition, duration, songList))
    }

    private fun getBitmapFromUrl(context: Context, url: String): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
