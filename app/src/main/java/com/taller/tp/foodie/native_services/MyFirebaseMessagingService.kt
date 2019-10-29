package com.taller.tp.foodie.native_services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.taller.tp.foodie.model.requestHandlers.UpdateFcmTokenRequestHandler
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.ui.ChatActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        UserService(this, UpdateFcmTokenRequestHandler()).updateUserFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // process data payload
        // Use data payload to create a notification
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            // Notification Channel for data notifications
            var name = ""
            var description = ""
            when (data["channelId"]) {
                "Chat Channel" -> {
                    name = "Chat"
                    description = "Canal de notificaciones de chat"
                    createNotificationChannel(data["channelId"], name, description)
                    sendChatDataNotification(data)
                    return
                }
            }

            createNotificationChannel(data["channelId"], name, description)

            // display notification immediately
            sendDataNotification(data)
        }
    }


    private fun sendChatDataNotification(data: Map<String, String>) {
        // TODO: check if chat activity is in foreground
        
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatActivity.CHAT_ID, data["group"])
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, data["channelId"] ?: error(""))
            .setAutoCancel(true)   //Automatically delete the notification
            .setSmallIcon(com.taller.tp.foodie.R.drawable.ic_stat_f) //Notification icon
            .setColor(resources.getColor(com.taller.tp.foodie.R.color.colorAccent))
            .setContentIntent(pendingIntent)
            .setContentTitle(data["title"])
            .setContentText(data["body"])
            .setGroup(data["group"])
            .setGroupSummary(true)
            .setSound(defaultSoundUri)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            data["group"],
            (data["group"] ?: error("")).hashCode(), notificationBuilder.build()
        )
    }


    private fun sendDataNotification(data: Map<String, String>) {
        /*
        val intent = Intent(this, MainView::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, data["channelId"] ?: error(""))
            .setAutoCancel(true)   //Automatically delete the notification
            .setSmallIcon(com.capitan.jpdfsaj.R.drawable.ic_notification) //Notification icon
            .setColor(resources.getColor(com.capitan.jpdfsaj.R.color.colorAccent))
            .setContentIntent(pendingIntent)
            .setContentTitle(data["title"])
            .setContentText(data["body"])
            .setLargeIcon(circularImage)
            .setGroup(data["group"])
            .setGroupSummary(true)
            .setSound(defaultSoundUri)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            data["group"],
            (data["group"] ?: error("")).hashCode(), notificationBuilder.build()
        )
         */
    }

    private fun createNotificationChannel(channelId: String?, name: String, description: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.enableVibration(true)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}