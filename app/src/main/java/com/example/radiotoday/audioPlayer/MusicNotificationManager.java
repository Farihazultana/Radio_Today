package com.example.radiotoday.audioPlayer;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import com.example.radiotoday.R;
import com.example.radiotoday.data.models.audio.Content;
import com.example.radiotoday.ui.activities.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MusicNotificationManager extends MediaSessionCompat.Callback {

    public static final int NOTIFICATION_ID = 101;
    public static final String PLAY_PAUSE_ACTION = "ebs.prabhatferi.PLAYPAUSE";
    public static final String NEXT_ACTION = "ebs.prabhatferi.NEXT";
    public static final String PREV_ACTION = "ebs.prabhatferi.PREV";
    private static NotificationManager mNotificationManager = null;
    private final String CHANNEL_ID = "ebs.prabhatferi.CHANNEL_ID";
    private final int REQUEST_CODE = 100;
    private final MusicService mMusicService;
    private final Context mContext;
    private MediaSessionCompat mediaSessionCompat;
    private NotificationCompat.Builder mNotificationBuilder;
    private Bitmap image;

    MusicNotificationManager(@NonNull final MusicService musicService) {
        mMusicService = musicService;
        mNotificationManager = (NotificationManager) mMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
        mContext = mMusicService.getBaseContext();

    }

    public static void removeNotification() {
        try {
            if (mNotificationManager != null) {
                mNotificationManager.cancelAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public final NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    @SuppressLint({"UnspecifiedImmutableFlag", "WrongConstant"})
    private PendingIntent playerAction(@NonNull final String action) {

        final Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getBroadcast(mMusicService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            return PendingIntent.getBroadcast(mMusicService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    @SuppressLint({"WrongConstant", "UnspecifiedImmutableFlag"})
    public Notification createNotification() {

        final Content song = MusicService.getMediaPlayerHolder().getCurrentSong();
        mNotificationBuilder = new NotificationCompat.Builder(mMusicService, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        final Intent openPlayerIntent = new Intent(mMusicService, MainActivity.class);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(mMusicService, REQUEST_CODE, openPlayerIntent, FLAG_MUTABLE);
        } else {
            contentIntent = PendingIntent.getActivity(mMusicService, REQUEST_CODE, openPlayerIntent, 0);
        }

        initMediaSession(song);

        mNotificationBuilder
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_audio)
                //.setLargeIcon(getLargeIcon())
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_pause))

                .setColor(ContextCompat.getColor(mContext, R.color.dark_red))
                .setContentTitle(song.getCatname())
                .setContentText(song.getAlbumname())
                .setContentIntent(contentIntent)
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mNotificationBuilder.setStyle(new MediaStyle()
                .setMediaSession(mediaSessionCompat.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2));
        return mNotificationBuilder.build();
    }

    @SuppressLint("ServiceCast")
    private void initMediaSession(Content song) {

        ComponentName mediaButtonReceiver = new ComponentName(mContext, CHANNEL_ID);
        mediaSessionCompat = new MediaSessionCompat(mContext,
                "AudioPlayer", // Debugging tag, any string
                mediaButtonReceiver,
                null);
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setCallback(this); // a MediaSessionCompat.Callback

// This is what enables media buttons and should be called
// Immediately after getting audio focus
        mediaSessionCompat.setActive(true);
        updateMetaData(song);
    }

    private void updateMetaData(Content song) {
        mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getLargeIcon(song))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtistname())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getCatname())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getAlbumname())
                .build());
    }

    @NonNull
    private NotificationCompat.Action notificationAction(@NonNull final String action) {

        int icon;

        switch (action) {
            default:
            case PREV_ACTION:
                icon = R.drawable.ic_previous;
                break;
            case PLAY_PAUSE_ACTION:

                icon = MusicService.getMediaPlayerHolder().getState() != PlaybackInfoListener.State.PAUSED ? R.drawable.ic_pause : R.drawable.ic_play;
                break;
            case NEXT_ACTION:
                icon = R.drawable.ic_next;
                break;
        }
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    @RequiresApi(26)
    private void createNotificationChannel() {

        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            final NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,
                            mMusicService.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(
                    mMusicService.getString(R.string.app_name));

            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(true);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Bitmap getLargeIcon(Content song) {

        try {
            URL url = new URL(song.getImage_location());
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }


        InputStream inputStream = null;
        try {
            inputStream = new URL(song.getImage_location()).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(inputStream);
        //return ;
    }
}
