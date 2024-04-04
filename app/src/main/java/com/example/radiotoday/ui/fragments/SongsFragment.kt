package com.example.radiotoday.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat.registerReceiver
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.radiotoday.databinding.FragmentSongsBinding
import com.example.radiotoday.utils.NotificationController
import com.example.radiotoday.utils.NotificationUtils
import com.example.radiotoday.utils.PlayAction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



@OptIn(UnstableApi::class)
class SongsFragment : BottomSheetDialogFragment() {
    private val notificationReceiver: NotificationController = NotificationController()

    private lateinit var mediaSession: MediaSessionCompat

    lateinit var binding: FragmentSongsBinding

    private var currentPosition : Long = 0L
    private var duration : Long = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressTask: Runnable = object : Runnable {
        override fun run() {
            updateSeekbar()
            handler.postDelayed(this, PROGRESS_UPDATE_INTERVAL)
        }
    }

    private val PROGRESS_UPDATE_INTERVAL = 1000L

    private val playbackStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                if (it is Activity) {
                    val isPlaying = intent?.getBooleanExtra("isPlaying", false) ?: false
                    updatePlayPauseButton(isPlaying)

                    NotificationUtils.updateNotification(requireActivity(), onPlayAction.isPlaying(), mediaSession, currentPosition, duration, "kk")
                }
            }
        }
    }

    var dismissListener: SongDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnPreDrawListener {
            val parent = view.parent as? View
            val params = parent?.layoutParams as? CoordinatorLayout.LayoutParams
            val behavior = params?.behavior

            if (behavior is BottomSheetBehavior<*>) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = false
            }

            true
        }

        binding.ivPlayerDown.setOnClickListener { dismiss() }

        //notification channel
        NotificationUtils.createNotificationChannel(requireActivity())

        val filter = IntentFilter().apply {
            addAction("Play")
            addAction("Pause")
            addAction("Next")
            addAction("Previous")
        }
        registerReceiver(requireActivity(),notificationReceiver, filter, RECEIVER_EXPORTED)
        registerReceiver(requireActivity(),playbackStateReceiver, IntentFilter("PlaybackState"), RECEIVER_EXPORTED)
        mediaSession = MediaSessionCompat(requireActivity(), "MusicPlayerService")



        binding.playerView.player = onPlayAction.getPlayer()


        onPlayAction.getPlayer().addListener(object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                Log.d("metadata", mediaMetadata.title.toString())

                binding.tvSong.text = mediaMetadata.title.toString()
                binding.tvSinger.text = mediaMetadata.albumArtist.toString()
            }
        })

        updateSeekbar()

        binding.ivPlay.setOnClickListener {
            binding.seekbarLive.visibility = View.VISIBLE
            binding.seekbarEnd.visibility = View.GONE
            binding.ivPause.visibility = View.VISIBLE
            binding.ivPlay.visibility = View.GONE
            binding.ivPlayNext.visibility = View.GONE
            binding.ivPlayPrev.visibility = View.GONE
            binding.ivShuffle.visibility = View.GONE
            binding.ivLoop.visibility = View.GONE

            onPlayAction.playMusic()
        }

        binding.ivPause.setOnClickListener {
            binding.seekbarLive.visibility = View.GONE
            binding.seekbarEnd.visibility = View.VISIBLE
            binding.ivPause.visibility = View.GONE
            binding.ivPlay.visibility = View.VISIBLE
            binding.ivPlayNext.visibility = View.VISIBLE
            binding.ivPlayPrev.visibility = View.VISIBLE
            binding.ivShuffle.visibility = View.VISIBLE
            binding.ivLoop.visibility = View.VISIBLE

            onPlayAction.pauseMusic()
        }

        binding.ivPlayNext.setOnClickListener {
            onPlayAction.nextMusic()

            //NotificationUtils.updateNotification(requireActivity(),onPlayAction.isPlaying(), mediaSession, currentPosition, duration)
        }

        binding.ivPlayPrev.setOnClickListener {
            onPlayAction.previousMusic()

        }

        binding.ivShuffle.setOnClickListener {
            onPlayAction.shuffleMusic()

        }

        binding.ivFavorite.setOnClickListener {
            binding.ivFavorite.visibility = View.GONE
            binding.ivNonFavorite.visibility = View.VISIBLE
        }

        binding.ivNonFavorite.setOnClickListener {
            binding.ivFavorite.visibility = View.VISIBLE
            binding.ivNonFavorite.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        //NotificationUtils.updateNotification(requireActivity(),onPlayAction.isPlaying(), mediaSession, currentPosition, duration)
        updatePlayPauseButton(onPlayAction.isPlaying())
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        handler.post(updateProgressTask)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateProgressTask)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onDismiss(dialog: DialogInterface) {
        dismissListener?.onSongDismissListener()
        super.onDismiss(dialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        //requireActivity().unregisterReceiver(notificationReceiver)
        requireActivity().unregisterReceiver(playbackStateReceiver)
    }

    override fun onDestroyView() {
        //onPlayAction.releasePlayer()
        super.onDestroyView()
    }

    interface SongDismissListener {
        fun onSongDismissListener()

    }

    private fun updateSeekbar(){
        currentPosition = onPlayAction.playerCurrentPosition()
        duration = onPlayAction.playerDuration()

        // Calculate minutes and seconds for current position
        val currentMinutes = currentPosition / 1000 / 60
        val currentSeconds = (currentPosition / 1000) % 60

        // Calculate minutes and seconds for total duration
        val totalMinutes = duration / 1000 / 60
        val totalSeconds = (duration / 1000) % 60

        Log.d("Player", "Current time: $currentMinutes:$currentSeconds, Total time: $totalMinutes:$totalSeconds")

        // Update UI elements
        binding.seekBar.progress = currentPosition.toInt()
        binding.seekBar.max = duration.toInt()
        binding.seekBarStart.text = String.format("%02d:%02d", currentMinutes, currentSeconds)
        binding.seekbarEnd.text = String.format("%02d:%02d", totalMinutes, totalSeconds)

    }


    private fun updatePlayPauseButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.seekbarLive.visibility = View.VISIBLE
            binding.seekbarEnd.visibility = View.GONE
            binding.ivPause.visibility = View.VISIBLE
            binding.ivPlay.visibility = View.GONE
            binding.ivPlayNext.visibility = View.GONE
            binding.ivPlayPrev.visibility = View.GONE
            binding.ivShuffle.visibility = View.GONE
            binding.ivLoop.visibility = View.GONE
        } else {
            binding.seekbarLive.visibility = View.GONE
            binding.seekbarEnd.visibility = View.VISIBLE
            binding.ivPause.visibility = View.GONE
            binding.ivPlay.visibility = View.VISIBLE
            binding.ivPlayNext.visibility = View.VISIBLE
            binding.ivPlayPrev.visibility = View.VISIBLE
            binding.ivShuffle.visibility = View.VISIBLE
            binding.ivLoop.visibility = View.VISIBLE
        }
    }

    companion object {
        lateinit var onPlayAction: PlayAction
            private set

        fun setOnPlayAction(action: PlayAction) {
            onPlayAction = action
        }
    }



}