
package com.example.radiotoday.audioPlayer;

import androidx.annotation.NonNull;

import com.example.radiotoday.data.models.audio.Content;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.List;

public interface PlayerController {

    void initMediaPlayer(@NonNull final Content song);

    void release();

    boolean isMediaPlayer();

    boolean isPlaying();

    void resumeOrPause();

    void playerPause();

    void reset();

    boolean isReset();

    void instantReset();

    Content getCurrentSong();

    void setCurrentSong(final Content currentTrack, final List<Content> currentTracksList);

    void skip(final boolean isNext);

    void seekTo(final int position);

    void seekBackTo10Sec();

    void seekFrowardTo10Sec();

    void setPlaybackInfoListener(final PlaybackInfoListener playbackInfoListener);

    @PlaybackInfoListener.State
    int getState();

    int getPlayerPosition();

    String getPlayTime();

    String getPlayDuration();

    int getDuration();

    void registerNotificationActionsReceiver(final boolean isRegister);

    ExoPlayer getMediaPlayer();

    void onResumeActivity();

    float getPlayBackSpeed();

    void setPlayBackSpeed(float playBackSpeed);

    int getSleepTime();

    void setSleepTime(int sleepTime);

    void startPlayCountTime();

    int getPlayCountTime();

    void resetPlayTime();

}
