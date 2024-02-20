package com.example.radiotoday.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.radiotoday.R
import com.example.radiotoday.ui.activities.AlarmActivity
import com.example.radiotoday.ui.activities.MainActivity
import com.example.radiotoday.ui.activities.ProfileActivity
import com.example.radiotoday.ui.activities.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.imageUrl
        message.notification?.tag
        message.notification?.eventTime
        message.notification?.link
        message.notification?.icon
        message.notification?.color
        message.notification?.notificationCount
        message.notification?.defaultSound
        message.data

        getFirebaseMessage(message.notification!!.title, message.notification!!.body , message.data)
    }

    private fun getFirebaseMessage(title: String?, body: String?, data: MutableMap<String, String>) {

        val type = data["section"]
        Log.i("FCM", "getFirebaseMessage: $type")

        /*val intent = when (type) {
            "profile" -> Intent(applicationContext, MainActivity::class.java)
            "alarm" -> Intent(applicationContext, MainActivity::class.java)
            else -> Intent(applicationContext, MainActivity::class.java)
        }*/

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("FCMType", type)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )



        val builder = NotificationCompat.Builder(this, "Notify")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val managerCompat = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        managerCompat.notify(102, builder.build())



    }

}