package com.sudoajay.stayawake.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.ui.setting.SettingsActivity.SettingsFragment.Companion.getIsStartOnBoot
import com.sudoajay.stayawake.ui.setting.SettingsActivity.SettingsFragment.Companion.getIsStartOnCarDock
import com.sudoajay.stayawake.ui.setting.SettingsActivity.SettingsFragment.Companion.getIsStartOnCharging
import com.sudoajay.stayawake.ui.setting.SettingsActivity.SettingsFragment.Companion.getIsStartOnDeskDock
import com.sudoajay.stayawake.ui.setting.SettingsActivity.SettingsFragment.Companion.getIsStartOnMobile
import com.sudoajay.stayawake.ui.setting.SettingsActivity.SettingsFragment.Companion.getIsStartOnUsb
import com.sudoajay.stayawake.ui.setting.SettingsActivity.SettingsFragment.Companion.getIsStartOnWifi
import com.sudoajay.stayawake.utill.ConnectivityType


class BootComplete : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val dockStatus: Intent? = IntentFilter(ACTION_DOCK_EVENT).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val dockState: Int = dockStatus?.getIntExtra(EXTRA_DOCK_STATE, -1) ?: -1

        val isCar: Boolean = dockState == EXTRA_DOCK_STATE_CAR
        val isDesk: Boolean = dockState == EXTRA_DOCK_STATE_DESK
                || dockState == EXTRA_DOCK_STATE_LE_DESK
                || dockState == EXTRA_DOCK_STATE_HE_DESK
        when (intent.action) {

            ACTION_BOOT_COMPLETED -> {
                if (getIsStartOnBoot(context)) StayAwakeService.startTheService(context)
            }
            ACTION_POWER_CONNECTED -> {
                if (getIsStartOnCharging(context) || (isCar && getIsStartOnCarDock(context)) || (isDesk && getIsStartOnDeskDock(
                        context
                    ))
                )
                    StayAwakeService.startTheService(context)
            }

            ACTION_POWER_DISCONNECTED -> {
                if (getIsStartOnCharging(context) || (!isCar && getIsStartOnCarDock(context)) || (!isDesk && getIsStartOnDeskDock(
                        context
                    ))
                )
                    StayAwakeService.stopTheService(context)
            }
            ACTION_UMS_CONNECTED -> {
                if (getIsStartOnUsb(context)) StayAwakeService.startTheService(context)
            }

            ACTION_UMS_DISCONNECTED -> {
                if (getIsStartOnUsb(context))
                    StayAwakeService.stopTheService(context)
            }
        }

        if (intent.action.toString() == "android.net.conn.CONNECTIVITY_CHANGE") {
            val type = ConnectivityType.getNetworkProvider(context)
            if ((getIsStartOnWifi(context) && type == context.getString(R.string.wifi_text))
                || (getIsStartOnMobile(context) && type == context.getString(R.string.mobile_data_text))
            )
                StayAwakeService.startTheService(context)
        }

    }
}
