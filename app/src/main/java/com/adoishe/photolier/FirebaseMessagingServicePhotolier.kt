package com.adoishe.photolier

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
//import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONObject





class FirebaseMessagingServicePhotolier : FirebaseMessagingService() {
    val TAG = "PushReceived"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.

        val params: Map<String?, String?>   = remoteMessage.data
        val receivedJSONObject              = JSONObject(params)

//        applicationContext.

//        Log.e(TAG, receivedJSONObject.toString())

//        val messageId = receivedJSONObject.optString("message_id"    , "")
//
//        val intent = Intent(applicationContext, MainActivity::class.java)
//
//        (baseContext as MainActivity).saveLog("got $messageId From: " + remoteMessage.from)

        sendNotification(remoteMessage , receivedJSONObject)

    }

    private  fun sendLastImageNotification (remoteMessage: RemoteMessage , receivedJSONObject : JSONObject){

    }

    private fun sendNotification(remoteMessage: RemoteMessage, receivedJSONObject : JSONObject) {

        val intent = Intent(applicationContext, MainActivity::class.java)



        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        intent.putExtra("orderId"       , receivedJSONObject.optString("orderId"    , ""));
        intent.putExtra("orderText"     , receivedJSONObject.optString("title"      , ""));
        intent.putExtra("messageId"     , receivedJSONObject.optString("message_id" , ""));
        intent.putExtra("orderName"     , receivedJSONObject.optString("orderName"  , ""));
        intent.putExtra("orderUuid"     , receivedJSONObject.optString("orderUuid"   , ""));
        intent.putExtra("orderStatus"   , receivedJSONObject.optString("orderStatus" , ""));

        intent.putExtra("receivedJSONObject"   , receivedJSONObject.toString());

//        getJSONObject("sendResult").toString());

        // FLAG_ACTIVITY_CLEAR_TASK
        //https://startandroid.ru/ru/uroki/vse-uroki-spiskom/190-urok-116-povedenie-activity-v-task-intent-flagi-launchmode-affinity.html



        var pendingIntentFlag = PendingIntent.FLAG_ONE_SHOT

        when (Build.VERSION.SDK_INT) {
            in 1..30    -> pendingIntentFlag = PendingIntent.FLAG_ONE_SHOT
            else        -> pendingIntentFlag = PendingIntent.FLAG_MUTABLE
        }

        val pendingIntent       = PendingIntent.getActivity(applicationContext
            , 11111 /* Request code */
            , intent
            , pendingIntentFlag)




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

            notificationChannel.description         = "Sample Channel description"
            notificationChannel.lightColor          = Color.RED
            notificationChannel.vibrationPattern    = longArrayOf(0, 1000, 500, 1000)

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(receivedJSONObject.getString("title"))
            .setContentText(receivedJSONObject.getString("content"))
            .setSmallIcon(R.drawable.button_bg_round)
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