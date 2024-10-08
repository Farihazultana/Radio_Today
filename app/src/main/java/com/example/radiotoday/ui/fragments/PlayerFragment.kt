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
import com.example.radiotoday.data.models.SongList
import com.example.radiotoday.databinding.FragmentPlayerBinding
import com.example.radiotoday.utils.NotificationUtils
import com.example.radiotoday.ui.interfaces.PlayAction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import java.util.regex.Pattern


@OptIn(UnstableApi::class)
class PlayerFragment() : BottomSheetDialogFragment() {
    private lateinit var mediaSession: MediaSessionCompat

    lateinit var binding: FragmentPlayerBinding

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

                    NotificationUtils.updateNotification(requireActivity(), onPlayAction.isPlaying(), currentPosition, duration, SongList.getSongsList())
                }
            }
        }
    }

    var dismissListener: PlayerDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)

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


        val filter = IntentFilter().apply {
            addAction("Play")
            addAction("Pause")
            addAction("Next")
            addAction("Previous")
        }
        //registerReceiver(requireActivity(),notificationReceiver, filter, RECEIVER_EXPORTED)
        registerReceiver(requireActivity(),playbackStateReceiver, IntentFilter("PlaybackState"), RECEIVER_EXPORTED)
        mediaSession = MediaSessionCompat(requireActivity(), "MusicPlayerService")



        binding.playerView.player = onPlayAction.getPlayer()



        onPlayAction.getPlayer().addListener(object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                Log.d("metadata", mediaMetadata.title.toString())

                /*binding.tvSong.text = mediaMetadata.title.toString()
                binding.tvSinger.text = mediaMetadata.albumArtist.toString()*/

                binding.tvSong.text = onPlayAction.getTitle()
                binding.tvSinger.text = onPlayAction.getArtist()

            }
        })

        updateSeekbar()

        binding.ivPlay.setOnClickListener {
            /*binding.seekbarLive.visibility = View.VISIBLE
            binding.seekbarEnd.visibility = View.GONE
            binding.ivPause.visibility = View.VISIBLE
            binding.ivPlay.visibility = View.GONE
            binding.ivPlayNext.visibility = View.GONE
            binding.ivPlayPrev.visibility = View.GONE
            binding.ivShuffle.visibility = View.GONE
            binding.ivLoop.visibility = View.GONE*/

            onPlayAction.playMusic()
        }

        binding.ivPause.setOnClickListener {
            /*binding.seekbarLive.visibility = View.GONE
            binding.seekbarEnd.visibility = View.VISIBLE
            binding.ivPause.visibility = View.GONE
            binding.ivPlay.visibility = View.VISIBLE
            binding.ivPlayNext.visibility = View.VISIBLE
            binding.ivPlayPrev.visibility = View.VISIBLE
            binding.ivShuffle.visibility = View.VISIBLE
            binding.ivLoop.visibility = View.VISIBLE*/

            onPlayAction.pauseMusic()
        }

        binding.ivPlayNext.setOnClickListener {
            onPlayAction.nextMusic()

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

        val songList = SongList.getSongsList()
        if (songList.isNotEmpty()) {
            val position = SongList.getCurrentPosition()
            var videoId = songList[position].url
            val embedCode = songList[position].embed_code
            Log.i("VideoPlayer", "onViewCreated: $embedCode")
            if (embedCode != null){
                binding.layoutPlayer.visibility = View.GONE
                binding.layoutYouTubePlayer.visibility = View.VISIBLE
                val youTubePlayerView = binding.youtubePlayerView
                lifecycle.addObserver(youTubePlayerView)

                if (embedCode != "Active"){
                    Log.i("VideoPlayer", "onViewCreated: $videoId")
                    if (videoId != null) {
                        videoId = extractVideoIdFromUrl(videoId)
                    }
                }

                youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {

                        Log.i("VideoPlayer", "onReady: $videoId")
                        if (videoId != null) {
                            youTubePlayer.loadVideo(videoId, 0f)
                        }
                    }
                })
            }else{
                binding.layoutPlayer.visibility = View.VISIBLE
                binding.layoutYouTubePlayer.visibility = View.GONE

            }
        }else{
            Log.i("TAG", "onViewCreated: Empty playlist")
        }



    }

    override fun onResume() {
        super.onResume()
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
        dismissListener?.onPlayerDismissListener()
        super.onDismiss(dialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(playbackStateReceiver)
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
            //binding.seekbarLive.visibility = View.VISIBLE
            //binding.seekbarEnd.visibility = View.GONE
            binding.ivPause.visibility = View.VISIBLE
            binding.ivPlay.visibility = View.GONE
            //binding.ivPlayNext.visibility = View.GONE
            //binding.ivPlayPrev.visibility = View.GONE
            //binding.ivShuffle.visibility = View.GONE
            //binding.ivLoop.visibility = View.GONE
        } else {
            //binding.seekbarLive.visibility = View.GONE
            //binding.seekbarEnd.visibility = View.VISIBLE
            binding.ivPause.visibility = View.GONE
            binding.ivPlay.visibility = View.VISIBLE
            //binding.ivPlayNext.visibility = View.VISIBLE
            //binding.ivPlayPrev.visibility = View.VISIBLE
            //binding.ivShuffle.visibility = View.VISIBLE
            //binding.ivLoop.visibility = View.VISIBLE
        }
    }

    private fun extractVideoIdFromUrl(videoUrl: String): String? {
        val pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2Fvideos%2F|youtu.be%2F|\\/v%2F)[^#\\&\\?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(videoUrl)

        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }
    }

    interface PlayerDismissListener {
        fun onPlayerDismissListener()

    }

    companion object {
        lateinit var onPlayAction: PlayAction
            private set

        fun setOnPlayAction(action: PlayAction) {
            onPlayAction = action
        }
    }



}