package com.example.radiotoday

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RadioTodayApplication : Application() {

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