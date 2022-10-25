package com.sudoajay.stayawake.ui.firebaseMessaging


import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.ui.mainActivity.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val url = data["Url"]
        if (remoteMessage.notification != null) {
            val notificationCompat: NotificationCompat.Builder =
                NotificationCompat.Builder(
                    applicationContext,
                    NotificationChannels.PUSH_NOTIFICATION
                )
            notificationCompat.setSmallIcon(R.drawable.ic_launcher_background)

            notificationCompat.setContentIntent(createPendingIntent(url.toString()))

            FirebaseNotification(applicationContext).notifyCompat(
                remoteMessage.notification,
                notificationCompat
            )
        }
    }

    private fun createPendingIntent(link: String): PendingIntent? {
        val flagUpdate =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT

        val flagOneShot =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_ONE_SHOT

        return if (link.isNotBlank() || link.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            PendingIntent.getActivity(
                this, 0, intent,
                flagUpdate
            )
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                flagOneShot
            )
        }
    }

    override fun onNewToken(token: String) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.e("MainActivityClass", token.toString())
    }


}