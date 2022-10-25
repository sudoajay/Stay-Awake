package com.sudoajay.stayawake.services

import android.app.NotificationManager
import android.app.NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import android.hardware.usb.UsbManager.*
import android.nfc.NfcAdapter.ACTION_ADAPTER_STATE_CHANGED
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.os.PowerManager
import android.os.PowerManager.ACTION_POWER_SAVE_MODE_CHANGED
import android.widget.Toast


class BroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val dockStatus: Intent? = IntentFilter(ACTION_DOCK_EVENT).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val dockState: Int = dockStatus?.getIntExtra(EXTRA_DOCK_STATE, -1) ?: -1

        val isCar: Boolean = dockState == EXTRA_DOCK_STATE_CAR
        val isDesk: Boolean = dockState == EXTRA_DOCK_STATE_DESK
                || dockState == EXTRA_DOCK_STATE_LE_DESK
                || dockState == EXTRA_DOCK_STATE_HE_DESK

//        when (intent.action) {
//
//
//            ACTION_BOOT_COMPLETED -> {
//                if (getIsStartOnBoot(context)) StayAwakeService.startTheService(context)
//            }
//            ACTION_POWER_CONNECTED -> {
//                if (getIsStartOnCharging(context) || (isCar && getIsStartOnCarDock(context)) || (isDesk && getIsStartOnDeskDock(
//                        context
//                    ))
//                )
//                    StayAwakeService.startTheService(context)
//            }
//
//            ACTION_POWER_DISCONNECTED -> {
//                if (getIsStartOnCharging(context) || (!isCar && getIsStartOnCarDock(context)) || (!isDesk && getIsStartOnDeskDock(
//                        context
//                    ))
//                )
//                    StayAwakeService.stopTheService(context)
//            }
//            ACTION_UMS_CONNECTED -> {
//                if (getIsStartOnUsb(context)) StayAwakeService.startTheService(context)
//            }
//
//            ACTION_UMS_DISCONNECTED -> {
//                if (getIsStartOnUsb(context))
//                    StayAwakeService.stopTheService(context)
//            }
//        }
//
//        if (intent.action.toString() == "android.net.conn.CONNECTIVITY_CHANGE") {
//            val type = ConnectivityType.getNetworkProvider(context)
//            if ((getIsStartOnWifi(context) && type == context.getString(R.string.wifi_text))
//                || (getIsStartOnMobile(context) && type == context.getString(R.string.mobile_data_text))
//            )
//                StayAwakeService.startTheService(context)
//        }




        val action = intent.action

        val device = intent.parcelable<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

        if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
            Toast.makeText(context, " Bluetooth Device is now connected", Toast.LENGTH_SHORT).show()
            StayAwakeService.startTheService(context)
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action || BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED == action) {
            Toast.makeText(context, " Bluetooth Device is now disconnected ", Toast.LENGTH_SHORT)
                .show()
            StayAwakeService.stopTheService(context)
        } else if (ACTION_USB_ACCESSORY_ATTACHED == action || ACTION_USB_DEVICE_ATTACHED == action) {
            Toast.makeText(context, " Usb Device is now connected", Toast.LENGTH_SHORT).show()
            StayAwakeService.startTheService(context)
        } else if (ACTION_USB_ACCESSORY_DETACHED == action || ACTION_USB_DEVICE_DETACHED == action) {
            Toast.makeText(context, " Usb Device is now disconnected ", Toast.LENGTH_SHORT).show()
            StayAwakeService.stopTheService(context)
        } else if (ACTION_BATTERY_LOW == action) {
            Toast.makeText(context, " Battery is low ", Toast.LENGTH_SHORT).show()
            StayAwakeService.stopTheService(context)

        } else if (ACTION_POWER_CONNECTED == action ) {
            Toast.makeText(context, " Power is now connected", Toast.LENGTH_SHORT).show()
            StayAwakeService.startTheService(context)
        } else if (ACTION_POWER_DISCONNECTED == action ) {
            Toast.makeText(context, " Power is now disconnected ", Toast.LENGTH_SHORT).show()
            StayAwakeService.stopTheService(context)
        } else if (ACTION_POWER_SAVE_MODE_CHANGED == action) {
            val powerManager =
                context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (powerManager.isPowerSaveMode) {
                Toast.makeText(context, " Power save mode is on ", Toast.LENGTH_SHORT).show()
                StayAwakeService.stopTheService(context)
            }


        }else if(ACTION_ADAPTER_STATE_CHANGED == action){
            // nfc
        }
        else if(ACTION_INTERRUPTION_FILTER_CHANGED == action){
                // do not disturb
        }
        else if(ACTION_AIRPLANE_MODE_CHANGED  == action){
            /// airplane mode
        }

        else if(ACTION_HEADSET_PLUG == action){
//        Headset
        }
        else if(ACTION_REBOOT == action){

        }
        else if(ACTION_BOOT_COMPLETED == action){

        }
        if (SDK_INT >= Build.VERSION_CODES.M) {
                if (action == NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED) {
                    val mNotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    when (mNotificationManager.currentInterruptionFilter) {
                        NotificationManager.INTERRUPTION_FILTER_ALARMS -> {
                            //do your stuff
                        }
                        NotificationManager.INTERRUPTION_FILTER_NONE -> {
                            //....
                        }
                        NotificationManager.INTERRUPTION_FILTER_ALL -> {
                            //....
                        }
                        NotificationManager.INTERRUPTION_FILTER_PRIORITY -> {
                            //....
                        }
                        NotificationManager.INTERRUPTION_FILTER_UNKNOWN -> {
                            //....
                        }
                    }
                }
            }



    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
}
