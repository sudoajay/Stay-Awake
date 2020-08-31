package com.sudoajay.stayawake.activity.transparent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.sudoajay.dnswidget.vpnClasses.Command
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.activity.main.MainActivity
import com.sudoajay.stayawake.helper.FlashlightProvider
import com.sudoajay.stayawake.services.StayAwakeService


class TransparentClass : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vpn_transparent_class)
        val flash = FlashlightProvider(applicationContext)

        if (!intent.action.isNullOrEmpty()) {
            val intent = intent
            when (intent.action) {
                MainActivity.startFlashId -> if(!MainActivity.isFlashActive(applicationContext))flash.turnFlashlightOn()
                MainActivity.stopFlashId -> if(MainActivity.isFlashActive(applicationContext))flash.turnFlashlightOff()
                MainActivity.startStayAwakeId -> if (!MainActivity.isStayAwakeActive(applicationContext)) startService()
                MainActivity.stopStayAwakeId -> if (MainActivity.isStayAwakeActive(applicationContext))stopService()
            }

            finish()
        }

    }

    private fun startService() {
        val startIntent = Intent(applicationContext, StayAwakeService::class.java)
        startIntent.putExtra("COMMAND", Command.START.ordinal)
        applicationContext.startService(startIntent)
    }


    private fun stopService() {

        val stopIntent = Intent(applicationContext, StayAwakeService::class.java)
        stopIntent.putExtra("COMMAND", Command.STOP.ordinal)
        applicationContext.startService(stopIntent)


    }
}