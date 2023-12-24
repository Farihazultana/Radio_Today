package com.example.radiotoday.audioPlayer;


import static com.example.radiotoday.audioPlayer.DemoUtil.DOWNLOAD_NOTIFICATION_CHANNEL_ID;
import static com.example.radiotoday.utils.Config.ACTION_KEY;
import static com.example.radiotoday.utils.Config.ACTION_NAME;
import static com.example.radiotoday.utils.Config.AUDIO_DOWNLOAD_PROGRESS_KEY;
import static com.example.radiotoday.utils.Config.AUDIO_DOWNLOAD_PROGRESS_VALUE;
import static com.example.radiotoday.utils.Config.DOWNLOAD_STATE;
import static com.example.radiotoday.utils.Config.DOWNLOAD_STATE_KEY;
import static com.example.radiotoday.utils.Config.STATE_COMPLETED;
import static com.example.radiotoday.utils.Config.STATE_FAILED;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.radiotoday.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

/**
 * A service for downloading media.
 */
public class FileDownloadExoService extends DownloadService {

    private static final int JOB_ID = 1;
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    public FileDownloadExoService() {
        super(
                FOREGROUND_NOTIFICATION_ID,
                DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                DOWNLOAD_NOTIFICATION_CHANNEL_ID,
                com.google.android.exoplayer2.source.smoothstreaming.R.string.exo_download_notification_channel_name,
                0);
    }

    @Override
    protected DownloadManager getDownloadManager() {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        DownloadManager downloadManager = DemoUtil.getDownloadManager(/* context= */ this);
        DownloadNotificationHelper downloadNotificationHelper =
                DemoUtil.getDownloadNotificationHelper(/* context= */ this);
        downloadManager.addListener(
                new TerminalStateNotificationHelper(
                        this, downloadNotificationHelper, FOREGROUND_NOTIFICATION_ID + 1));
        return downloadManager;
    }

    @Override
    protected Scheduler getScheduler() {
        return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
    }

    @SuppressLint("SwitchIntDef")
    @Override
    protected Notification getForegroundNotification(
            List<Download> downloads, @Requirements.RequirementFlags int notMetRequirements) {

        for (int i = 0; i < downloads.size(); i++) {
            Download download = downloads.get(i);
            switch (download.state) {
                case Download.STATE_RESTARTING:
                case Download.STATE_DOWNLOADING:
                    float downloadPercentage = download.getPercentDownloaded();
                    if (downloadPercentage != C.PERCENTAGE_UNSET) {
                        Intent intent = new Intent(ACTION_NAME);
                        intent.putExtra(ACTION_KEY, AUDIO_DOWNLOAD_PROGRESS_KEY);
                        intent.putExtra(AUDIO_DOWNLOAD_PROGRESS_VALUE, downloadPercentage);
                        sendBroadcast(intent);
                    }
            }
        }
        return DemoUtil.getDownloadNotificationHelper(/* context= */ this)
                .buildProgressNotification(
                        this,
                        R.drawable.ic_down,
                        null,
                        null,
                        downloads,
                        notMetRequirements);
    }

    /**
     * Creates and displays notifications for downloads when they complete or fail.
     *
     * <p>This helper will outlive the lifespan of a single instance of {@link FileDownloadExoService}.
     * It is static to avoid leaking the first {@link FileDownloadExoService} instance.
     */
    private static final class TerminalStateNotificationHelper implements DownloadManager.Listener {

        private final Context context;
        private final DownloadNotificationHelper notificationHelper;

        private int nextNotificationId;

        public TerminalStateNotificationHelper(
                Context context, DownloadNotificationHelper notificationHelper, int firstNotificationId) {
            this.context = context.getApplicationContext();
            this.notificationHelper = notificationHelper;
            nextNotificationId = firstNotificationId;
        }

        @Override
        public void onDownloadChanged(
                DownloadManager downloadManager, Download download, @Nullable Exception finalException) {
            Notification notification;

            Intent intent = new Intent(ACTION_NAME);
            intent.putExtra(ACTION_KEY, DOWNLOAD_STATE_KEY);

            if (download.state == Download.STATE_COMPLETED) {
                notification =
                        notificationHelper.buildDownloadCompletedNotification(
                                context,
                                R.drawable.ic_audio,
                                null,
                                Util.fromUtf8Bytes(download.request.data));
                intent.putExtra(DOWNLOAD_STATE, STATE_COMPLETED);
            } else if (download.state == Download.STATE_FAILED) {
                notification =
                        notificationHelper.buildDownloadFailedNotification(
                                context,
                                R.drawable.ic_bell,
                                null,
                                Util.fromUtf8Bytes(download.request.data));
                intent.putExtra(DOWNLOAD_STATE, STATE_FAILED);
            } else {
                intent.putExtra(DOWNLOAD_STATE, STATE_FAILED);
                return;
            }

            context.sendBroadcast(intent);

            NotificationUtil.setNotification(context, nextNotificationId++, notification);
        }
    }
}
