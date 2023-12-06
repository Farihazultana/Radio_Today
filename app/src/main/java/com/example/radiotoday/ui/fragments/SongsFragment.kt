package com.example.radiotoday.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.example.radiotoday.R
import com.example.radiotoday.databinding.FragmentSongsBinding
import com.example.radiotoday.utils.BlurTransformation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SongsFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentSongsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongsBinding.inflate(inflater, container, false)

        Glide.with(this)
            .load(R.drawable.album_cover)
            .apply(bitmapTransform(BlurTransformation(25, 3)))
            .placeholder(R.drawable.album_cover)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivPlayerBG)

        Glide.with(this)
            .load(R.drawable.album_cover)
            .placeholder(R.drawable.album_cover)
            .transform(RoundedCorners(16))
            .into(binding.imageView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnPreDrawListener {
            val parent = view.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior

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

}