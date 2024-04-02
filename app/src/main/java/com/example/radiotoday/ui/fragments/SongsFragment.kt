package com.example.radiotoday.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.radiotoday.R
import com.example.radiotoday.databinding.FragmentSongsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SongsFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentSongsBinding

    var dismissListener: SongDismissListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongsBinding.inflate(inflater, container, false)

        /*Glide.with(this)
            .load(R.drawable.album_cover)
            .apply(bitmapTransform(BlurTransformation(25, 3)))
            .placeholder(R.drawable.album_cover)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivPlayerBG)*/

        /*Glide.with(this)
            .load(R.drawable.album_cover)
            .placeholder(R.drawable.album_cover)
            .transform(RoundedCorners(16))
            .into(binding.imageView)*/



        binding.ivPlay.setOnClickListener {
            binding.seekbarLive.visibility = View.VISIBLE
            binding.seekbarEnd.visibility = View.GONE
            binding.ivPause.visibility = View.VISIBLE
            binding.ivPlay.visibility = View.GONE
            binding.ivPlayNext.visibility = View.GONE
            binding.ivPlayPrev.visibility = View.GONE
            binding.ivShuffle.visibility = View.GONE
            binding.ivLoop.visibility = View.GONE
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
        }

        binding.ivFavorite.setOnClickListener {
            binding.ivFavorite.visibility = View.GONE
            binding.ivNonFavorite.visibility = View.VISIBLE
        }

        binding.ivNonFavorite.setOnClickListener {
            binding.ivFavorite.visibility = View.VISIBLE
            binding.ivNonFavorite.visibility = View.GONE
        }

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

    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onDismiss(dialog: DialogInterface) {
        dismissListener?.onSongDismissListener()
        super.onDismiss(dialog)
    }

    interface SongDismissListener {
        fun onSongDismissListener()

    }



}