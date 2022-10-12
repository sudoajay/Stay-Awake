package com.sudoajay.stayawake.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.ui.mainActivity.MainActivity
import com.sudoajay.stayawake.ui.BaseActivity
import com.sudoajay.stayawake.ui.darkMode.DarkModeBottomSheet
import com.sudoajay.stayawake.ui.feedbackAndHelp.SendFeedbackAndHelp
import com.sudoajay.stayawake.utill.DeleteCache
import com.sudoajay.stayawake.utill.HelperClass.Companion.isDarkMode
import java.util.*

class SettingsActivity : BaseActivity() {

    private var isDarkTheme: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isDarkTheme = isDarkMode(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                false

        }
        setContentView(R.layout.activity_settings)

        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                onBackPressedButton()
            }
        } else {
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        onBackPressedButton()
                    }
                })
        }

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedButton()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private val ratingLink =
            "https://play.google.com/store/apps/details?id=com.sudoajay.duplication_data"


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey)


            val useDarkTheme =
                findPreference("useDarkTheme") as Preference?
            useDarkTheme!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                showDarkMode()
                true
            }

            val selectLanguage =
                findPreference("changeLanguage") as Preference?
            selectLanguage?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                openSelectLanguage()
                true
            }


            val clearCache =
                findPreference("clearCache") as Preference?
            clearCache!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                DeleteCache.deleteCache(requireContext())
                true
            }

            val privacyPolicy =
                findPreference("privacyPolicy") as Preference?
            privacyPolicy!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                openPrivacyPolicy()
                true
            }

            val sendFeedback =
                findPreference("sendFeedback") as Preference?
            sendFeedback!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                sendFeedback()
                true
            }

            val reportABug =
                findPreference("reportABug") as Preference?
            reportABug!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                openGithubForReport()
                true
            }

            val shareApp =
                findPreference("shareApp") as Preference?
            shareApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                shareApp()
                true
            }
            val rateUs =
                findPreference("rateUs") as Preference?
            rateUs!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                rateUs()
                true
            }
            val moreApp =
                findPreference("moreApp") as Preference?
            moreApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                moreApp()
                true
            }
            val aboutApp =
                findPreference("aboutApp") as Preference?
            aboutApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                openGithubApp()
                true
            }


        }
        private fun showDarkMode() {
            val darkModeBottomSheet = DarkModeBottomSheet(MainActivity.settingId)
            darkModeBottomSheet.show(
                childFragmentManager.beginTransaction(),
                "darkModeBottomSheet"
            )

        }

        private fun openPrivacyPolicy() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }


        private fun openGithubForReport() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }

        private fun shareApp() {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Link-Share")
            i.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareMessage) + " - git " + ratingLink)
            startActivity(Intent.createChooser(i, "Share via"))
        }

         private fun rateUs() {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(ratingLink)
            startActivity(i)
        }

        private fun openSelectLanguage() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(openFragmentID, openSelectLanguageID)
            startActivity(intent)
        }

        private fun moreApp() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }
        private fun openGithubApp() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }
        private fun sendFeedback(){
            val intent = Intent(requireContext(), SendFeedbackAndHelp::class.java)
            startActivity(intent)
        }



        companion object {


            fun getIsStartOnBoot(context: Context): Boolean {
                return PreferenceManager
                    .getDefaultSharedPreferences(context).getBoolean("start_on_boot", false)
            }

            fun getIsStartOnCharging(context: Context): Boolean {
                return PreferenceManager
                    .getDefaultSharedPreferences(context).getBoolean("start_on_charging", false)
            }

            fun getIsStartOnUsb(context: Context): Boolean {
                return PreferenceManager
                    .getDefaultSharedPreferences(context).getBoolean("start_on_usb", false)
            }

            fun getIsStartOnCarDock(context: Context): Boolean {
                return PreferenceManager
                    .getDefaultSharedPreferences(context).getBoolean("start_on_car_dock", false)
            }

            fun getIsStartOnDeskDock(context: Context): Boolean {
                return PreferenceManager
                    .getDefaultSharedPreferences(context).getBoolean("start_on_desk_dock", false)
            }

            fun getIsStartOnWifi(context: Context): Boolean {
                return PreferenceManager
                    .getDefaultSharedPreferences(context).getBoolean("start_on_wifi", false)
            }

            fun getIsStartOnMobile(context: Context): Boolean {
                return PreferenceManager
                    .getDefaultSharedPreferences(context).getBoolean("start_on_mobile", false)
            }
        }
    }

    private fun onBackPressedButton() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}