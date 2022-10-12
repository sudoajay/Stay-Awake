package com.sudoajay.stayawake.ui.mainActivity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.databinding.ActivityMainBinding
import com.sudoajay.stayawake.helper.BrightnessClass
import com.sudoajay.stayawake.services.Command
import com.sudoajay.stayawake.services.StayAwakeService
import com.sudoajay.stayawake.ui.BaseActivity
import com.sudoajay.stayawake.ui.darkMode.DarkModeBottomSheet
import com.sudoajay.stayawake.ui.setting.SettingsActivity
import com.sudoajay.stayawake.utill.HelperClass.Companion.isDarkMode
import com.sudoajay.stayawake.utill.Toaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {
    private var isDarkTheme: Boolean = false
    lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var brightnessClass: BrightnessClass
    private var doubleBackToExitPressedOnce = false

    // Boolean to check if our activity is bound to service or not
    var mIsBound: Boolean = false

    var mService: StayAwakeService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkTheme = isDarkMode(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    true
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewmodel = viewModel
        binding.mainActivity = this
        binding.lifecycleOwner = this

        if (!intent.action.isNullOrEmpty() && intent.action == settingId)
            openMoreSetting()



        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                onBack()
            }
        } else {
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        onBack()
                    }
                })
        }

    }


    override fun onResume() {
        brightnessClass = BrightnessClass(this)

        setItemColor()

        binding.displaySeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                brightnessClass.updateBrightness(progress)

            }
        })

        viewModel.flashLight.value = isFlashActive(applicationContext)

        callStayAwakeFun(isStayAwakeActive(applicationContext))

        super.onResume()
    }

    private fun setItemColor() {

        viewModel.flashLight.observe(this) {
            if (it) {
                binding.flashlightImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primaryAppColor

                    )
                )
            } else {
                binding.flashlightImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primaryAppColor
                    )
                )
            }

        }

        viewModel.sos.observe(this) {

            if (it) {
                binding.sosTextView.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primaryAppColor

                    )
                )
            } else {
                binding.sosTextView.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.mainItemColor
                    )
                )
            }

        }

        viewModel.displayController.observe(this) {
            if (it) {
                binding.displayControllerImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primaryAppColor

                    )
                )



                if (brightnessClass.checkSystemWritePermission()) {
                    brightnessClass.callBeforeEverything()
                    binding.displaySeekBar.progress = brightnessClass.brightness
                }
            } else {
                binding.displayControllerImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.mainItemColor
                    )
                )
            }

        }


        //         Setup BottomAppBar Navigation Setup
        binding.bottomAppBar.navigationIcon?.mutate()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.setTint(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.navigationIcon
                    )
                )
            }
            binding.bottomAppBar.navigationIcon = it
        }


        setSupportActionBar(binding.bottomAppBar)

    }


    fun onClickStayAwake() {
        val newIt = binding.stayAwakeFloatingActionButton.drawable.constantState ==
                ContextCompat.getDrawable(this, R.drawable.ic_stay_awake_on)!!.constantState

        if (!checkAndRequestWakeLockPermission()) {
            callStayAwakeFun(false)
            return
        }

        callStayAwakeFun(newIt)

        if (!newIt && !isStayAwakeActive(applicationContext)) startService()
        else if (!newIt && isStayAwakeActive(applicationContext)) stopService()


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> showNavigationDrawer()
            R.id.display_setting_optionMenu ->
                startActivityForResult(
                    Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS),
                    OPEN_DISPLAY_SETTING
                )
            R.id.darkMode_optionMenu -> showDarkMode()

            R.id.default_setting_optionMenu -> {
                viewModel.defaultSetting()
                defaultValue()
            }

            R.id.more_app_setting_optionMenu -> openMoreSetting()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun showNavigationDrawer() {
        val navigationDrawerBottomSheet = NavigationDrawerBottomSheet()
        navigationDrawerBottomSheet.show(supportFragmentManager, navigationDrawerBottomSheet.tag)
    }

    private fun defaultValue() {


        if (isStayAwakeActive(applicationContext)) stopService()
        callStayAwakeFun(false)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun openMoreSetting() {
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun showDarkMode() {
        val darkModeBottomSheet = DarkModeBottomSheet(homeId)
        darkModeBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            darkModeBottomSheet.tag
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_WAKE_LOCK) {

//            Permission not granted
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toaster.showToast(
                    applicationContext,
                    getString(R.string.giveUsPermission)
                )
            }
        }
    }

    private fun checkAndRequestWakeLockPermission(): Boolean {
        val permit =
            PermissionChecker.checkSelfPermission(
                applicationContext,
                Manifest.permission.WAKE_LOCK
            )

        if (permit != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WAKE_LOCK),
                PERMISSION_REQUEST_WAKE_LOCK
            )

            return false
        }

        return true // Permission is granted already
    }

    private fun startService() {
        val startIntent = Intent(applicationContext, StayAwakeService::class.java)
        startIntent.putExtra("COMMAND", Command.START.ordinal)
        applicationContext.bindService(startIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        applicationContext.startService(startIntent)
    }

    private fun stopService() {

        val stopIntent = Intent(applicationContext, StayAwakeService::class.java)
        stopIntent.putExtra("COMMAND", Command.STOP.ordinal)
        applicationContext.bindService(stopIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        applicationContext.startService(stopIntent)

        if (mIsBound) {

            applicationContext.unbindService(serviceConnection)
            mIsBound = false
        }
    }


    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {

            // We've bound to MyService, cast the IBinder and get MyBinder instance
            val binder = iBinder as StayAwakeService.MyBinder
            mService = binder.service
            mIsBound = true
            getStatus() // return a random number from the service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {

            mIsBound = false
        }
    }

    /**
     * Method for listening to random numbers generated by our service class
     */
    private fun getStatus() {
        mService!!.stayAwakeStatus.observe(this) {
            callStayAwakeFun(it)
        }
    }

    private fun callStayAwakeFun(newIt: Boolean) {
        binding.stayAwakeFloatingActionButton.setImageResource(if (newIt) R.drawable.ic_stay_awake_on else R.drawable.ic_stay_awake_off)
        if (newIt) {
            binding.stayAwakeFloatingActionButton.setColorFilter(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.primaryAppColor

                )
            )
        } else {
            binding.stayAwakeFloatingActionButton.setColorFilter(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.mainItemColor
                )
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        if (mIsBound) {
            applicationContext.unbindService(serviceConnection)
            mIsBound = false
        }
    }


    private fun onBack() {
        if (doubleBackToExitPressedOnce) {
            closeApp()
            return
        }
        doubleBackToExitPressedOnce = true
        Toaster.showToast(applicationContext, "Click Back Again To Exit")
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            doubleBackToExitPressedOnce = false
        }
    }

    private fun closeApp() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }


    companion object {
        const val homeId = "homeShortcut"
        const val settingId = "settingShortcut"
        const val startFlashId = "startFlashShortcut"
        const val stopFlashId = "stopFlashShortcut"
        const val startStayAwakeId = "startStayAwakeShortcut"
        const val stopStayAwakeId = "stopStayAwakeShortcut"


        const val PERMISSION_REQUEST_WAKE_LOCK = 0
        const val OPEN_DISPLAY_SETTING = 10

        fun isStayAwakeActive(context: Context): Boolean {
            return context.getSharedPreferences(
                "state",
                Context.MODE_PRIVATE
            ).getBoolean(context.getString(R.string.is_stay_awake_active_text), false)
        }

        fun isFlashActive(context: Context): Boolean {
            return context.getSharedPreferences(
                "state",
                Context.MODE_PRIVATE
            ).getBoolean(context.getString(R.string.is_flash_active_text), false)
        }
    }
}