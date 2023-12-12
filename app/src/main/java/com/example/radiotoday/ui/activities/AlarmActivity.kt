package com.example.radiotoday.ui.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.radiotoday.R
import com.example.radiotoday.databinding.ActivityAlarmBinding
import com.example.radiotoday.utils.AlarmReceiver
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar

class AlarmActivity : AppCompatActivity() {
    lateinit var binding: ActivityAlarmBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        calendar = Calendar.getInstance()

        binding.ivAddAlarmButton.setOnClickListener {

            showTimePicker()

        }

        binding.btnSetAlarm.setOnClickListener {

            setAlarm()

        }

        binding.btnCancelAlarm.setOnClickListener {

            cancelAlarm()

        }
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker
            .Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager, "alarmID")

        picker.addOnPositiveButtonClickListener {

            if (picker.hour >= 12) {
                binding.tvSelectedTime.text =
                    String.format("%02d", picker.hour - 12) + ":" + String.format(
                        "%02d",
                        picker.minute
                    ) + "PM"
            } else {
                binding.tvSelectedTime.text =
                    String.format("%02d", picker.hour) + ":" + String.format(
                        "%02d",
                        picker.minute
                    ) + "AM"
            }

            //calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }

    private fun setAlarm() {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        Log.d("MainActivity", "setAlarm: calendar time - ${calendar.time}")
        Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show()

    }

    private fun cancelAlarm() {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Radio Today"
            val description = "Radio Today using Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarmID", name, importance)
            channel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}