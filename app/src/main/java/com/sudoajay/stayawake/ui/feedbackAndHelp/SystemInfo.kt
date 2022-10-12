package com.sudoajay.stayawake.ui.feedbackAndHelp


import android.app.Activity
import android.os.Build
import android.os.Debug
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.utill.ConnectivityType
import com.sudoajay.stayawake.utill.FileSize
import java.io.IOException
import java.util.*

class SystemInfo(private var activity: Activity) {


    fun getAppVersionName():Int{
       val packageInfo =  activity.packageManager.getPackageInfo(
            activity.packageName, 0)

        return PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
    }


    fun getInfo(str: String): String {
        when (str) {
            "MANUFACTURER" -> return Build.MANUFACTURER.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
            "MODEL" -> return Build.MODEL
            "PRODUCT" -> return Build.PRODUCT
            "SDK_INT" -> return Build.VERSION.SDK_INT.toString()
        }
        return ""
    }

    fun getLanguage(): String {
        return Locale.getDefault().displayLanguage
    }

    fun getHeapTotalSize(): Long {
        return Debug.getNativeHeapSize()
    }

    fun getHeapFreeSize(): Long {
        return Debug.getNativeHeapFreeSize()
    }

    fun getScreenSize(): DisplayMetrics {
        return activity.resources.displayMetrics
    }

    fun createTextForEmail(): StringBuilder {
        val writer = StringBuilder("")
        try {


            writer.append("\n\n\n ===== ${activity.getString(R.string.system_info_text)} =====")
            writer.append(
                "\n\n ${activity.getString(R.string.device_text)} : " + getInfo("MANUFACTURER") + "  " + getInfo(
                    "MODEL"
                ) + " (" + getInfo("PRODUCT") + ")"
            )
            writer.append("\n ${activity.getString(R.string.os_api_level_text)} : " + getInfo("SDK_INT"))
            writer.append("\n ${activity.getString(R.string.app_version_text)} : " + getAppVersionName())
            writer.append("\n ${activity.getString(R.string.language_text)} : " + getLanguage())
            writer.append(
                "\n ${activity.getString(R.string.total_memory_text)} : " + FileSize.convertIt(
                    getHeapTotalSize()
                )
            )
            writer.append(
                "\n ${activity.getString(R.string.free_memory_text)} : " + FileSize.convertIt(
                    getHeapFreeSize()
                )
            )
            writer.append(
                " \n ${activity.getString(R.string.screen_Text)} : " + getScreenSize().heightPixels.toString() + " x " + getScreenSize().widthPixels.toString()
            )
            writer.append(
                "\n ${activity.getString(R.string.network_type_text)} : " + ConnectivityType.getNetworkProvider(
                    activity
                )
            )
        } catch (e: IOException) {
            Log.e("ErrorTag", "createTextForEmail ${e.localizedMessage}")

        }
        return writer
    }


}