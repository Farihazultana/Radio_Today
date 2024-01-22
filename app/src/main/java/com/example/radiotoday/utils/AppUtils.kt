package com.example.radiotoday.utils

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import com.example.radiotoday.R

object AppUtils {
    const val BASE_URL = "http://192.168.110.196/api/"

    const val LogInStatus = "LOGIN_STATUS"

    fun setDialog(context: Context, layoutId: Int): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(layoutId)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)
        dialog.window!!.attributes!!.windowAnimations = R.style.animation

        dialog.show()
        return dialog
    }
}