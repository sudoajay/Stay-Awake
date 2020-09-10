package com.sudoajay.stayawake.helper

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Window
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.activity.main.MainActivity


class BrightnessClass(private var mainActivity: MainActivity) {
    //Variable to store brightness value
    var brightness = 0

    //Content resolver used as a handle to the system's settings
    private var cResolver: ContentResolver? = null

    //Window object, that will store a reference to the current window
    private var window: Window? = null


    fun callBeforeEverything() {
        //Get the content resolver

        //Get the content resolver
        cResolver = mainActivity.contentResolver

//Get the current window

//Get the current window
        window = mainActivity.window

        try {
            // To handle the auto
            Settings.System.putInt(
                cResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
            //Get the current system brightness
            brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Exception) {
            CustomToast.toastIt(
                mainActivity.applicationContext,
                mainActivity.getString(R.string.reportIt)
            )
        }
    }

    fun updateBrightness(progress: Int) {
        //Set the system brightness using the brightness variable value
        //Set the system brightness using the brightness variable value
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, progress)
        //Get the current window attributes
        //Get the current window attributes
        val layoutpars = window!!.attributes
        //Set the brightness of this window
        //Set the brightness of this window
        layoutpars.screenBrightness = progress / 255.toFloat()
        //Apply attribute changes to this window
        //Apply attribute changes to this window
        window!!.attributes = layoutpars
    }

    fun checkSystemWritePermission(): Boolean {
        var retVal = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(mainActivity.applicationContext)
            if (!retVal) {
                mainActivity.viewModel.displayController.value = false
                CustomToast.toastIt(
                    mainActivity.applicationContext,
                    mainActivity.getString(R.string.giveUsPermission)
                )
                openAndroidPermissionsMenu()
                return false
            }
        }
        return retVal
    }


    private fun openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + mainActivity.packageName)
            mainActivity.startActivity(intent)
        }

    }

}