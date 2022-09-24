package com.adoishe.photolier

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
//import android.support.annotation.RequiresApi
import androidx.annotation.RequiresApi

//@RequiresApi(Build.VERSION_CODES.O)
class NotificationUtils(base: Context) : ContextWrapper(base) {

    val nManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

  //  @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val myChannel = NotificationChannel(CHANNEL_ID,
            CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
            enableLights(true)
            enableVibration(true)
            lightColor = Color.GREEN
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        nManager.createNotificationChannel(myChannel)
    }

    companion object {
        const val CHANNEL_ID = "photolier.app.CHANNEL_ID"
        const val CHANNEL_NAME = "photolier.app.Notification"
    }
}