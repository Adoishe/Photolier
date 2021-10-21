package com.adoishe.photolier

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.iid.FirebaseInstanceId
import android.R
import android.app.Notification
import org.json.JSONObject





class FirebaseMessagingServicePhotolier : FirebaseMessagingService() {
    val TAG = "PushReceived"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage!!.from)
        //Log.d(TAG, "Notification Message Body: " + p0.notification?.body!!)

        val params: Map<String?, String?> = remoteMessage.data
        val receivedJSONobject = JSONObject(params)
        Log.e(TAG, receivedJSONobject.toString())

        sendNotification(remoteMessage , receivedJSONobject)
        /*
        val intent = Intent(applicationContext, MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("message", p0.notification?.body!!)

        startActivity(intent)


         */



    }

    private fun sendNotification(remoteMessage: RemoteMessage , receivedJSONobject : JSONObject) {

        val intent = Intent(applicationContext, MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent       = PendingIntent.getActivity(applicationContext
                                                    , 11111 /* Request code */
                                                    , intent
                                                    , PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri     = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val NOTIFICATION_CHANNEL_ID = "tutorialspoint_01"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Photolier",
                NotificationManager.IMPORTANCE_MAX
            )
            // Configure the notification channel.
            notificationChannel.description = "Sample Channel description"

            notificationChannel.enableLights(true)
            notificationChannel.lightColor          = Color.RED
            notificationChannel.vibrationPattern    = longArrayOf(0, 1000, 500, 1000)

            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }



        val notificationBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(receivedJSONobject.getString("title"))
            .setContentText(receivedJSONobject.getString("content"))
            .setSmallIcon(R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            //.setChannel(channelId)
            /*
            //.setContentText(remoteMessage.notification?.body)
            //.setContentText("eeeeeeeeeeeeeeeeeeeeeeeeeeeee")
            //.setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_media_play)

            //.setSound(defaultSoundUri)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setContentIntent(pendingIntent)

             */

        //val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(111111 /* ID of notification */
                                    , notificationBuilder.build())



 


    }
    companion object {
        const val CHANNEL_ID = "photolier.app.CHANNEL_ID"
    }
}