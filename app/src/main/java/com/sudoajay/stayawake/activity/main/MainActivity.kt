package com.sudoajay.stayawake.activity.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.sudoajay.dnswidget.vpnClasses.Command
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.activity.BaseActivity
import com.sudoajay.stayawake.databinding.ActivityMainBinding
import com.sudoajay.stayawake.helper.BrightnessClass
import com.sudoajay.stayawake.helper.CustomToast
import com.sudoajay.stayawake.helper.DarkModeBottomSheet
import com.sudoajay.stayawake.helper.NotificationChannels.notificationOnCreate
import com.sudoajay.stayawake.services.StayAwakeService


class MainActivity : BaseActivity() {
    private var isDarkTheme: Boolean = false
    lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"
    private lateinit var brightnessClass: BrightnessClass

    // Boolean to check if our activity is bound to service or not
    var mIsBound: Boolean = false

    var mService: StayAwakeService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkTheme = isDarkMode(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        changeStatusBarColor()

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewmodel = viewModel
        binding.mainActivity = this
        binding.lifecycleOwner = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationOnCreate(applicationContext)
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

        val isStayAwakeActive = getSharedPreferences(
            "state",
            Context.MODE_PRIVATE
        ).getBoolean(getString(R.string.is_stay_awake_active_text), false)

        callStayAwakeFun(isStayAwakeActive)

        super.onResume()
    }

    private fun setItemColor() {

        viewModel.flashLight.observe(this, {
            if (it) {
                binding.flashlightImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.primaryAppColorNight else R.color.primaryAppColorNoNight

                    )
                )
            } else {
                binding.flashlightImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.mainItemColorNight else R.color.mainItemColorNoNight
                    )
                )
            }

        })

        viewModel.sos.observe(this, {

            if (it) {
                binding.sosTextView.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.primaryAppColorNight else R.color.primaryAppColorNoNight

                    )
                )
            } else {
                binding.sosTextView.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.mainItemColorNight else R.color.mainItemColorNoNight
                    )
                )
            }

        })

        viewModel.displayController.observe(this, {
            if (it) {
                binding.displayControllerImageView.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.primaryAppColorNight else R.color.primaryAppColorNoNight

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
                        if (isDarkTheme) R.color.mainItemColorNight else R.color.mainItemColorNoNight
                    )
                )
            }

        })


        //         Setup BottomAppBar Navigation Setup
        binding.bottomAppBar.navigationIcon?.mutate()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.setTint(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.navigationIconDarkColor else R.color.navigationIconColor
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
        val isStayAwakeActive = getSharedPreferences(
            "state",
            Context.MODE_PRIVATE
        ).getBoolean(getString(R.string.is_stay_awake_active_text), false)

        Log.e(
            TAG, newIt.toString() + " new it ==  " + isStayAwakeActive.toString() + " ----" +
                    " " + checkAndRequestWakeLockPermission().toString()
        )

        if (!checkAndRequestWakeLockPermission()) {
            callStayAwakeFun(false)
            return
        }

        callStayAwakeFun(newIt)

        if (!newIt && !isStayAwakeActive) startService()
        else if (!newIt && isStayAwakeActive) stopService()


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
            }
            R.id.darkMode_optionMenu -> showDarkMode()

            R.id.refresh_optionMenu -> showDarkMode()

            R.id.more_setting_optionMenu -> {
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
                CustomToast.toastIt(
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
            Log.e(TAG, " got Unibind")
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
            Log.d(TAG, "ServiceConnection: connected to service.")
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            val binder = iBinder as StayAwakeService.MyBinder
            mService = binder.service
            mIsBound = true
            getStatus() // return a random number from the service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "ServiceConnection: disconnected from service.")
            mIsBound = false
        }
    }

    /**
     * Method for listening to random numbers generated by our service class
     */
    private fun getStatus() {
        mService!!.stayAwakeStatus.observe(this, {
            callStayAwakeFun(it)
        })
    }

    private fun callStayAwakeFun(newIt: Boolean) {
        binding.stayAwakeFloatingActionButton.setImageResource(if (newIt) R.drawable.ic_stay_awake_on else R.drawable.ic_stay_awake_off)
        if (newIt) {
            binding.stayAwakeFloatingActionButton.setColorFilter(
                ContextCompat.getColor(
                    applicationContext,
                    if (isDarkTheme) R.color.primaryAppColorNight else R.color.primaryAppColorNoNight

                )
            )
        } else {
            binding.stayAwakeFloatingActionButton.setColorFilter(
                ContextCompat.getColor(
                    applicationContext,
                    if (isDarkTheme) R.color.mainItemColorNight else R.color.navigationIconColor
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


    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }

    companion object {
        const val homeId = "home"

        const val PERMISSION_REQUEST_WAKE_LOCK = 0
    }
}