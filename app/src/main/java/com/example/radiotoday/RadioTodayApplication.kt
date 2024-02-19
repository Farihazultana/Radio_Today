package com.example.radiotoday

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RadioTodayApplication : Application() {

    val FCM_CHANNEL_ID = "Notify"
    private val applicationName: String
        get() {
            val stringId = this.applicationInfo.labelRes
            return this.getString(stringId)
        }

    override fun onCreate() {
        super.onCreate()
        if (instance == null) {
            instance = this
        }
        Companion.applicationContext = applicationContext
        val appName = applicationName

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val fcmChannel = NotificationChannel(
                FCM_CHANNEL_ID,
                "FCM_Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(fcmChannel)
        }
    }

    private val isNetworkConnected: Boolean
        get() {
            val connMgr = instance!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connMgr.activeNetworkInfo
            if (activeNetworkInfo != null) { // connected to the internet
                if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true
                } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    return true
                }
            }
            return false
        }

    companion object {
        private var instance: RadioTodayApplication? = null
        var applicationContext: Context? = null
        fun hasNetwork(): Boolean {
            return instance!!.isNetworkConnected
        }
    }

}