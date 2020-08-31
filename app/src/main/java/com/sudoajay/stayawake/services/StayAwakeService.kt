package com.sudoajay.stayawake.services

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.sudoajay.dnswidget.vpnClasses.Command
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.activity.main.MainActivity
import com.sudoajay.stayawake.helper.ConnectivityType
import com.sudoajay.stayawake.helper.NotificationChannels
import com.sudoajay.stayawake.helper.NotificationChannels.notificationOnCreate

class StayAwakeService : Service() {

    private var mBinder: IBinder = MyBinder()
    private var wakeLock: PowerManager.WakeLock? = null
    private var TAG = "StayAwakeService"
    private lateinit var notificationCompat: NotificationCompat.Builder
    private var notification: Notification? = null
    var stayAwakeStatus = MutableLiveData<Boolean>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        when (if (intent == null) Command.START else Command.values()[intent.getIntExtra(
            "COMMAND",
            Command.START.ordinal
        )]) {
            Command.RESUME -> {
                stayAwakeStatus.value = true
                Log.i(TAG, "onStartCommand  Command.RESUME -  $intent")

                getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                    .putBoolean(getString(R.string.is_stay_awake_active_text), true).apply()
                closeNotification()
//
                startService()


            }
            Command.START -> {
                Log.i(TAG, "onStartCommand  Command.Start -  $intent")

                getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                    .putBoolean(getString(R.string.is_stay_awake_active_text), true).apply()
                startService()
                stayAwakeStatus.value = true

            }
            Command.STOP -> {
                Log.i(TAG, "onStartCommand  Command.Stop -  ")
//
                stopVpn()
                stopSelf()

            }
            Command.PAUSE -> {

                stayAwakeStatus.value = false
                Log.i(TAG, "onStartCommand  Command.PAUSE -  ")
                getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                    .putBoolean(getString(R.string.is_stay_awake_active_text), false).apply()
                pauseService()

            }
        }
        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()

        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.FULL_WAKE_LOCK, "Stay Awake::MyWakelockTag")
            }
    }

    private fun startService() {

        notification = Notification(applicationContext)

        notificationCompat =
            NotificationCompat.Builder(
                applicationContext,
                NotificationChannels.SERVICE_RUNNING
            )
        notificationCompat.setSmallIcon(R.drawable.ic_stay_awake_on)

        notificationCompat.setContentIntent(createPendingIntent())

        notification!!.notifyCompat(
            notificationCompat
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startForeground(NOTIFICATION_ID_STATE, notificationCompat.build(), 1)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NOTIFICATION_ID_STATE, notificationCompat.build())

       startStayAwake()
    }


    private fun startStayAwake() {
        Log.e(TAG, "Start Wake Lock")
        wakeLock!!.acquire(1000*60*1000L /*1000 minutes*/)
    }



    private fun createPendingIntent(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun pauseService() {
        stopVpn()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            NOTIFICATION_ID_STATE,
            NotificationCompat.Builder(this, NotificationChannels.SERVICE_PAUSED)
                .setSmallIcon(R.drawable.ic_stay_awake_off) // TODO: Notification icon
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getString(R.string.notification_paused_title))
                .setContentText(getString(R.string.notification_paused_text))
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .setContentIntent(
                    getResumePendingIntent()
                )
                .build()
        )



    }

    private fun getResumePendingIntent(): PendingIntent {

        return PendingIntent.getService(
            applicationContext,
            REQUEST_CODE_START,
            Intent(applicationContext, StayAwakeService::class.java)
                .putExtra("COMMAND", Command.RESUME.ordinal),
            0
        )
    }


    private fun stopVpn() {

        stopForeground(true)

        notification?.notificationManager?.cancelAll()

        stayAwakeStatus.value = false
        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(getString(R.string.is_stay_awake_active_text), false).apply()

        stopStayAwake()

    }

    private fun stopStayAwake() {
        Log.e(TAG, "Stop Wake Lock")
        if (wakeLock != null && wakeLock!!.isHeld)
            wakeLock!!.release()
    }
    private fun closeNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }


    override fun onDestroy() {
        Log.i(TAG, "Destroyed, shutting down")
        stopVpn()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }


    inner class MyBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        val service: StayAwakeService
            get() =// Return this instance of MyService so clients can call public methods
                this@StayAwakeService
    }

    companion object {
        const val REQUEST_CODE_START = 43
        const val REQUEST_CODE_PAUSE = 42
        const val REQUEST_CODE_STOP = 41
        const val NOTIFICATION_ID_STATE = 10

        const val EXTRA_START_AWAKE_FLAG =
            "vyan.alwaysonwidget.services.StayAwakeService.EXTRA_START_AWAKE_FLAG" // Boolean extra

        const val ACTION_TOGGLE_STAY_AWAKE =
            "vyan.alwaysonwidget.services.StayAwakeService.ACTION_TOGGLE_STAY_AWAKE"
        const val ACTION_ANNOUNCE_STAY_AWAKE_STATE =
            "vyan.alwaysonwidget.services.StayAwakeService.ACTION_ANNOUNCE_STAY_AWAKE_STATE"

        const val ACTION_STAY_AWAKE_STATE_CHANGED =
            "vyan.alwaysonwidget.services.StayAwakeService.ACTION_STAY_AWAKE_STATE_CHANGED"
        const val EXTRA_STAY_AWAKE_STATE =
            "vyan.alwaysonwidget.services.StayAwakeService.EXTRA_STAY_AWAKE_STATE" // Boolean extra


        fun startTheService(context: Context) {
            Log.i("BOOT", "Checking whether to start ad buster on boot")

            if (context.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(context.getString(R.string.is_stay_awake_active_text), false)
            ) {
                return
            }

            Log.i("BOOT", "Starting ad buster from boot")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationOnCreate(context)
            }
            val intent = getStartIntent(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, StayAwakeService::class.java)
            intent.putExtra("COMMAND", Command.START.ordinal)
            return intent
        }

        fun stopTheService(context: Context){
            if (!context.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(context.getString(R.string.is_stay_awake_active_text), false)
            ) {
                return
            }

            val intent = getStopIntent(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private fun getStopIntent(context: Context): Intent {
            val intent = Intent(context, StayAwakeService::class.java)
            intent.putExtra("COMMAND", Command.STOP.ordinal)
            return intent
        }
    }
}