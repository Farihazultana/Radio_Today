package com.example.radiotoday.audioPlayer;

import static android.content.Context.AUDIO_SERVICE;
import static com.example.radiotoday.utils.Config.ACTION_KEY;
import static com.example.radiotoday.utils.Config.ACTION_NAME;
import static com.example.radiotoday.utils.Config.ACTION_NEXT;
import static com.example.radiotoday.utils.Config.ENABLE_INTERNET;
import static com.example.radiotoday.utils.Config.DOWNLOAD_STATE;
import static com.example.radiotoday.utils.Config.DOWNLOAD_STATE_KEY;
import static com.example.radiotoday.utils.Config.PLAY_TIME_CHANGE;
import static com.example.radiotoday.utils.Config.PREVIEW_ENDED;
import static com.example.radiotoday.utils.Config.SOMETHING_WENT_WRONG;
import static com.example.radiotoday.utils.Config.STATE_BUFFERING;
import static com.example.radiotoday.utils.Config.STATE_COMPLETED;
import static com.example.radiotoday.utils.Config.STATE_READY;
import static com.example.radiotoday.utils.Config.TRACK_ID;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.radiotoday.RadioTodayApplication;
import com.example.radiotoday.data.models.audio.Content;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.util.EventLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MediaPlayerHolder implements PlayerController, Player.Listener, PlayBackControl {

    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    private static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    private static final float VOLUME_NORMAL = 1.0f;
    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;
    private final Context mContext;
    private final MusicService mMusicService;
    private final AudioManager mAudioManager;
    private ExoPlayer mPlayer, instantPlayer;
    private PlaybackInfoListener mPlaybackInfoListener;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekBarPositionUpdateTask;
    private boolean sReplaySong = false;
    private @PlaybackInfoListener.State
    int mState;
    private NotificationReceiver mNotificationActionsReceiver;
    private MusicNotificationManager mMusicNotificationManager;
    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
    private Content mSelectedSong;
    private List<Content> mSongs;
    private final int escTime = 10000;
    private boolean sPlayOnFocusGain;
    private float playBackSpeed = 1.0f;
    private int sleepTime = 0;
    private boolean isPLayingAudio;
    private final Handler handler = new Handler();
    private int playTime = 0;
    private boolean isStateIdle = false;
    private boolean appStartFirstTime = true;
    private long stateIdlePosition;
    private final ArrayList<Content> downloadTrackList = new ArrayList<>();
    private FragmentManager fragmentManager;
    private RenderersFactory renderersFactory;
    private DownloadTracker downloadTracker;
    private String bookCode;


    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                @Override
                public void onAudioFocusChange(final int focusChange) {

                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mCurrentAudioFocusState = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost audio focus, but will gain it back (shortly), so note whether
                            // playback should resume
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            sPlayOnFocusGain = isMediaPlayer() && mState == PlaybackInfoListener.State.PLAYING || mState == PlaybackInfoListener.State.RESUMED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently"
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    if (mPlayer != null) {
                        // Update the player state based on the change
                        configurePlayerState();
                    }
                }
            };


    MediaPlayerHolder(@NonNull final MusicService musicService) {
        mMusicService = musicService;
        mContext = mMusicService.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
    }

    private void registerActionsReceiver() {
        mNotificationActionsReceiver = new NotificationReceiver();
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(MusicNotificationManager.PREV_ACTION);
        intentFilter.addAction(MusicNotificationManager.PLAY_PAUSE_ACTION);
        intentFilter.addAction(MusicNotificationManager.NEXT_ACTION);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        mMusicService.registerReceiver(mNotificationActionsReceiver, intentFilter);

        mContext.registerReceiver(broadcastReceiver, new IntentFilter(ACTION_NAME));
    }

    private void unregisterActionsReceiver() {
        if (mMusicService != null && mNotificationActionsReceiver != null) {
            try {
                mMusicService.unregisterReceiver(mNotificationActionsReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Content getCurrentSong() {
        return mSelectedSong;
    }

    @Override
    public void registerNotificationActionsReceiver(final boolean isReceiver) {

        if (isReceiver) {
            registerActionsReceiver();
        } else {
            unregisterActionsReceiver();
        }
    }

    @Override
    public void setCurrentSong(@NonNull final Content song, @NonNull final List<Content> songs) {
        mSelectedSong = song;
        mSongs = songs;
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {

        if (playbackState == Player.STATE_ENDED) {
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
                mPlaybackInfoListener.onPlaybackCompleted();
            }

            if (sReplaySong) {
                if (isMediaPlayer()) {
                    resetSong();
                }
                sReplaySong = false;
            } else {
                skip(true);
            }

        } else if (playbackState == Player.STATE_BUFFERING) {
            Intent intent = new Intent(ACTION_NAME);
            intent.putExtra(ACTION_KEY, STATE_BUFFERING);
            mContext.sendBroadcast(intent);

        } else if (playbackState == Player.STATE_READY) {
            startUpdatingCallbackWithPosition();
            Intent intent = new Intent(ACTION_NAME);
            intent.putExtra(ACTION_KEY, STATE_READY);
            mContext.sendBroadcast(intent);

        } else if (playbackState == Player.STATE_IDLE) {
            isStateIdle = true;
            setStatus(PlaybackInfoListener.State.PAUSED);
            stateIdlePosition = mPlayer.getCurrentPosition();
            if (!appStartFirstTime) {
                if (RadioTodayApplication.Companion.hasNetwork()) {
                    Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, ENABLE_INTERNET, Toast.LENGTH_SHORT).show();
                }
            }
            appStartFirstTime = false;
        }

        if (getCurrentSong() != null) {
            Intent i = new Intent(ACTION_NAME);
            i.putExtra(ACTION_KEY, PLAY_TIME_CHANGE);
            i.putExtra(TRACK_ID, getCurrentSong().getCatcode());
            mContext.sendBroadcast(i);
        }
    }


    @Override
    public void onResumeActivity() {
        startUpdatingCallbackWithPosition();
    }

    @Override
    public float getPlayBackSpeed() {
        return playBackSpeed;
    }

    @Override
    public void setPlayBackSpeed(float speed) {
        this.playBackSpeed = speed;
        PlaybackParameters param = new PlaybackParameters(speed);
        mPlayer.setPlaybackParameters(param);
    }

    @Override
    public int getSleepTime() {
        return sleepTime;
    }

    @Override
    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public int getPlayCountTime() {
        return playTime;
    }

    @Override
    public void resetPlayTime() {
        playTime = 0;
    }

    @Override
    public void startPlayCountTime() {
        playtimeCount();
    }

    private void playtimeCount() {
        if (isPlaying()) {
            playTime++;
        }
        //Log.e("playTime", "----------------" + playTime);
        handler.postDelayed(this::playtimeCount, 1000);
    }

    private void tryToGetAudioFocus() {

        final int result = mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    public void setPlaybackInfoListener(@NonNull final PlaybackInfoListener listener) {
        mPlaybackInfoListener = listener;
    }

    private void setStatus(final @PlaybackInfoListener.State int state) {

        mState = state;
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(state);

            Intent i = new Intent(ACTION_NAME);
            i.putExtra(ACTION_KEY, STATE_READY);
            mContext.sendBroadcast(i);
        }
    }

    private void resumeMediaPlayer() {
        if (!isPlaying() && mMusicNotificationManager != null) {

            if (isStateIdle && RadioTodayApplication.Companion.hasNetwork() ) {
                initMediaPlayer(getCurrentSong());
                mPlayer.seekTo(stateIdlePosition);
                isStateIdle = false;
            } else {
                setStatus(PlaybackInfoListener.State.RESUMED);
            }

            mPlayer.play();
            mMusicService.startForeground(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());

            Intent i = new Intent(ACTION_NAME);
            i.putExtra(ACTION_KEY, PLAY_TIME_CHANGE);
            i.putExtra(TRACK_ID, getCurrentSong().getCatcode());
            mContext.sendBroadcast(i);
        }
    }

    private void pauseMediaPlayer() {

        if (mMusicNotificationManager != null && mPlayer != null) {
            setStatus(PlaybackInfoListener.State.PAUSED);
            mPlayer.pause();
            mMusicService.stopForeground(false);
            mMusicNotificationManager.getNotificationManager().notify(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());

            Intent i = new Intent(ACTION_NAME);
            i.putExtra(ACTION_KEY, PLAY_TIME_CHANGE);
            i.putExtra(TRACK_ID, getCurrentSong().getCatcode());
            mContext.sendBroadcast(i);
        }
    }

    private void resetSong() {
        mPlayer.seekTo(0);
        mPlayer.play();
        setStatus(PlaybackInfoListener.State.PLAYING);
    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekBarPositionUpdateTask == null) {
            mSeekBarPositionUpdateTask = this::updateProgressCallbackTask;
        }

        mExecutor.scheduleAtFixedRate(mSeekBarPositionUpdateTask, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void updateProgressCallbackTask() {
        if (isMediaPlayer() && mPlayer.isPlaying()) {
            int currentPosition = (int) mPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void instantReset() {
        if (isMediaPlayer()) {
            if (mPlayer.getCurrentPosition() < 5000) {
                skip(false);
            } else {
                resetSong();
            }
        }
    }

    @Override
    public void initMediaPlayer(@NonNull final Content song) {

        try {

            Uri mUri = Uri.parse(mSelectedSong.getUrl());
            MediaItem mediaItem = MediaItem.fromUri(mUri);

            if (mPlayer != null) {
                mPlayer.release();
            }

            mPlayer = new ExoPlayer.Builder(mContext).build();

            RenderersFactory renderersFactory = DemoUtil.buildRenderersFactory(mContext, true);

            MediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(DemoUtil.getDataSourceFactory(mContext));

            DefaultTrackSelector trackSelector = new DefaultTrackSelector(mContext);
            //lastSeenTracksInfo = TracksInfo.EMPTY;
            mPlayer = new ExoPlayer.Builder(mContext, renderersFactory)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .setTrackSelector(trackSelector)
                    .build();
            //mPlayer.setTrackSelectionParameters(trackSelectionParameters);
            //mPlayer.addListener(new PlayerEventListener());
            mPlayer.addAnalyticsListener(new EventLogger(trackSelector));
            //mPlayer.setAudioAttributes(AudioAttributes.DEFAULT, true);
            mPlayer.setPlayWhenReady(false);
            //mPlayer.setPlayer(player);

            //boolean haveStartPosition = startItemIndex != C.INDEX_UNSET;
            //if (haveStartPosition) {
            //mPlayer.seekTo(startItemIndex, startPosition);
            //}
            mPlayer.setMediaItems(Collections.singletonList(mediaItem), false);
            mPlayer.prepare();


            /*DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(mediaItem);
            mPlayer.setForegroundMode(true);
            mPlayer.setMediaSource(mediaSource);
            mPlayer.prepare();
            mPlayer.setPlayWhenReady(false);*/

            mMusicNotificationManager = mMusicService.getMusicNotificationManager();
            mPlayer.addListener(this);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExoPlayer getMediaPlayer() {
        return mPlayer;
    }

    @Override
    public void release() {
        if (isMediaPlayer()) {
            mPlayer.release();
            mPlayer = null;
            giveUpAudioFocus();
            unregisterActionsReceiver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mMusicService.stopForeground(Service.STOP_FOREGROUND_REMOVE);
            } else {
                mMusicService.stopSelf();
            }
        }
    }

    @Override
    public boolean isPlaying() {
        return isMediaPlayer() && mPlayer.isPlaying();
    }

    @Override
    public void resumeOrPause() {

        if (instantPlayer != null && instantPlayer.isPlaying()) {
            instantPlayer.pause();
        }

        if (isPlaying()) {
            pauseMediaPlayer();
        } else {
            resumeMediaPlayer();
        }
        tryToGetAudioFocus();
    }

    @Override
    public void playerPause() {
        pauseMediaPlayer();
    }

    @Override
    public @PlaybackInfoListener.State
    int getState() {
        return mState;
    }

    @Override
    public boolean isMediaPlayer() {
        return mPlayer != null;
    }

    @Override
    public void reset() {
        sReplaySong = !sReplaySong;
    }

    @Override
    public boolean isReset() {
        return sReplaySong;
    }

    @Override
    public void skip(final boolean isNext) {
        getSkipSong(isNext);
        Intent i = new Intent(ACTION_NAME);
        i.putExtra(ACTION_KEY, ACTION_NEXT);
        i.putExtra(TRACK_ID, getCurrentSong().getCatcode());
        mContext.sendBroadcast(i);
    }

    private void getSkipSong(final boolean isNext) {
        int currentIndex = 0;
        int index;
        for (Content chapter : mSongs) {
            if (chapter.getCatcode().equals(mSelectedSong.getCatcode())) {
                break;
            }
            currentIndex++;
        }

        try {
            if (isNext) {
                index = currentIndex + 1;
            } else {
                index = currentIndex - 1;
            }

            mSelectedSong = mSongs.get(index);
        } catch (IndexOutOfBoundsException e) {
            mSelectedSong = currentIndex != 0 ? mSongs.get(0) : mSongs.get(mSongs.size() - 1);
            e.printStackTrace();
        }
        initMediaPlayer(mSelectedSong);
        resumeOrPause();
    }

    @Override
    public void seekTo(final int position) {
        if (isMediaPlayer()) {
            mPlayer.seekTo(position);
        }
    }

    @Override
    public void seekBackTo10Sec() {
        seekTo(Math.max(getPlayerPosition() - escTime, 0));
    }

    @Override
    public void seekFrowardTo10Sec() {
        long currentPosition = getPlayerPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + escTime <= getDuration()) {
            // forward song
            seekTo((int) (currentPosition + escTime));
        } else {
            // forward to end position
            seekTo(getDuration());
        }
    }

    @Override
    public int getPlayerPosition() {
        int currentPosition = 0;
        if (mPlayer != null) {
            currentPosition = (int) mPlayer.getCurrentPosition();
        }
        return currentPosition;
    }

    @Override
    public String getPlayTime() {
        return createTimeLabel(getPlayerPosition());
    }

    @Override
    public String getPlayDuration() {
        return createTimeLabel((int) mPlayer.getDuration());
    }

    @Override
    public int getDuration() {
        return (int) mPlayer.getDuration();
    }

    /**
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the MediaPlayer instance respecting the current audio focus state. So if we
     * have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */
    private void configurePlayerState() {

        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            isPLayingAudio = isPlaying();
            // We don't have audio focus and can't duck, so we have to pause
            if (isPLayingAudio) {
                pauseMediaPlayer();
            }

            if (instantPlayer != null && instantPlayer.isPlaying()) {
                instantPlayer.pause();
            }

        } else {

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mPlayer.setVolume(VOLUME_DUCK);
            } else {
                mPlayer.setVolume(VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (sPlayOnFocusGain) {
                if (isPLayingAudio) {
                    resumeMediaPlayer();
                    sPlayOnFocusGain = false;
                }
            }
        }
    }

    @Override
    public void onPlay() {
        if (isPlaying()) {
            pauseMediaPlayer();
        } else {
            resumeMediaPlayer();
        }
    }

    @Override
    public void onPause() {

    }

    public static String createTimeLabel(int time) {

        String timeLabel;
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }

    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();

            if (action != null) {

                switch (action) {
                    case MusicNotificationManager.PREV_ACTION:
                        instantReset();
                        break;
                    case MusicNotificationManager.PLAY_PAUSE_ACTION:
                        resumeOrPause();
                        break;
                    case MusicNotificationManager.NEXT_ACTION:
                        skip(true);

                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (mSelectedSong != null) {
                            pauseMediaPlayer();
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        if (mSelectedSong != null && !isPlaying()) {
                            resumeMediaPlayer();
                        }
                        break;
                    case Intent.ACTION_HEADSET_PLUG:
                        if (mSelectedSong != null) {
                            switch (intent.getIntExtra("state", -1)) {
                                //0 means disconnected
                                case 0:
                                    pauseMediaPlayer();
                                    break;
                                //1 means connected
                                case 1:
                                    if (!isPlaying()) {
                                        resumeMediaPlayer();
                                    }
                                    break;
                            }
                        }
                        break;
                    case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                        if (isPlaying()) {
                            pauseMediaPlayer();
                        }
                        break;
                }
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString(ACTION_KEY);

            if (DOWNLOAD_STATE_KEY.equals(action)) {
                String downloadState = intent.getExtras().getString(DOWNLOAD_STATE);

                if (downloadState.equals(STATE_COMPLETED)) {
                    if (downloadTrackList.size() > 0) {
                        downloadTrackList.remove(0);
                        if (!downloadTrackList.isEmpty()) {
                            //downloadAudioBookChapter(downloadTrackList);
                        }
                    }
                }
            }
        }
    };

}
