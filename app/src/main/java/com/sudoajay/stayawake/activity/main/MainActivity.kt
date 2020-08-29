package com.sudoajay.stayawake.activity.main

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.activity.BaseActivity
import com.sudoajay.stayawake.databinding.ActivityMainBinding
import com.sudoajay.stayawake.helper.DarkModeBottomSheet

class MainActivity : BaseActivity() {
    private var isDarkTheme: Boolean = false
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private var Tag = "MainActivity"
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
        binding.lifecycleOwner = this


    }

    override fun onResume() {
        setItemColor()
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                it.setTint(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.navigationIconDarkColor else R.color.navigationIconColor
                    )
                )
            }
            binding.bottomAppBar.navigationIcon = it
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
            }
            R.id.darkMode_optionMenu -> showDarkMode()
            R.id.refresh_optionMenu -> {
            }
            R.id.more_setting_optionMenu -> {
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun showDarkMode() {
        val darkModeBottomSheet = DarkModeBottomSheet(homeId)
        darkModeBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            darkModeBottomSheet.tag
        )

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
    }
}