package com.sudoajay.stayawake.utill

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.ui.BaseActivity.Companion.changeLanguageValueId
import com.sudoajay.stayawake.ui.BaseActivity.Companion.darkModeId
import com.sudoajay.stayawake.ui.BaseActivity.Companion.isDarkModeId
import com.sudoajay.stayawake.ui.BaseActivity.Companion.isFirstTimeNotifyId
import java.util.*


class HelperClass {

    companion object {


        fun getLanguage(context: Context): String {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(changeLanguageValueId, getLocalLanguage(context))
                .toString()
        }

        fun setLanguage(context: Context, value: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString(changeLanguageValueId, value)
            editor.apply()
        }

        fun isFirstTimeNotify(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(isFirstTimeNotifyId, true)
        }

        fun setFirstTimeNotify(context: Context, value: Boolean) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putBoolean(isFirstTimeNotifyId, value)
            editor.apply()
        }


        private fun getLocalLanguage(context: Context): String {
            val lang = Locale.getDefault().language
            val array = context.resources.getStringArray(R.array.languageValues)
            return if (lang in array) lang else "en"
        }


        fun throwToaster(context: Context, value: String?) {
            Toaster.showToast(context, value ?: "")
        }


        fun setIsDarkModeValue(context: Context, isDarkMode: Boolean) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putBoolean(isDarkModeId, isDarkMode)
            editor.apply()
        }

        fun setDarkModeValue(context: Context, darkMode: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString(darkModeId, darkMode)
            editor.apply()
        }

        fun getDarkMode(context: Context): String {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(darkModeId, context.getString(R.string.system_default_text)).toString()
        }

        fun isDarkMode(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(isDarkModeId, false)
        }


    }
}