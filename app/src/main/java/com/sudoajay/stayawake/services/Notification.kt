package com.sudoajay.stayawake.services


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.model.Command


class Notification(private val context: Context) {
    var notificationManager: NotificationManager? = null
    private var notification: Notification? = null



    fun notifyCompat(
        builder: NotificationCompat.Builder
    ) { // local variable

//        Pending Intent For Pause Action
        val pausePendingIntent = PendingIntent.getService(
            context, StayAwakeService.REQUEST_CODE_PAUSE, Intent(context, StayAwakeService::class.java)
                .putExtra("COMMAND", Command.PAUSE.ordinal), 0
        )

//        Pending Intent For Stop Action
        val stopPendingIntent = PendingIntent.getService(
            context, StayAwakeService.REQUEST_CODE_STOP, Intent(context, StayAwakeService::class.java)
                .putExtra("COMMAND", Command.STOP.ordinal), 0
        )

        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        // Default ringtone
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        builder

            .addAction(
                R.drawable.ic_pause,
                context.getString(R.string.notification_action_pause),
                pausePendingIntent
            )

            .addAction(
                R.drawable.ic_stop, context.getString(R.string.stop_text),
                stopPendingIntent
            )

            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(Notification.DEFAULT_ALL) // Set required fields, including the small icon, the
            .setContentTitle(context.getString(R.string.awake_is_running_text))
            .setContentText(context.getString(R.string.click_to_open_app_text))
            .setOngoing(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSound(uri) // Provide a large icon, shown with the notification in the

            .color = ContextCompat.getColor(context, R.color.appTheme)
        // If this notification relates to a past or upcoming event, you

        //Content hen expanded


        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags =
            notification!!.flags or (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT)

        notifyNotification(notification!!)
    }

     private fun notifyNotification(notification: Notification) {
        notificationManager!!.notify(StayAwakeService.NOTIFICATION_ID_STATE, notification)

    }



}