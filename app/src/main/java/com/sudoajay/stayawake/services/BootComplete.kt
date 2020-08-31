package com.sudoajay.stayawake.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sudoajay.stayawake.activity.settingActivity.SettingsActivity.SettingsFragment.Companion.getIsStartOnBoot


class BootComplete : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (getIsStartOnBoot(context))
            StayAwakeService.checkStartVpnOnBoot(context)
    }
}
