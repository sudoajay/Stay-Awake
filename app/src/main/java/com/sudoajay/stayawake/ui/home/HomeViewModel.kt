package com.sudoajay.stayawake.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.model.HomeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private var _application = application

    var search = ""
    var homeOptionList = mutableListOf<HomeOption>()


    init {

        homeOptionList.add( HomeOption(1,R.string.device_timeout_text,R.drawable.ic_timer,R.string.device_timeout_summary_text , isOn = false, isShowSwitch = false ))
        homeOptionList.add( HomeOption(2,R.string.always_text,R.drawable.ic_always,R.string.always_summary_text , isOn = false, isShowSwitch = true))

        homeOptionList.add( HomeOption(3,R.string.power_charging_text,R.drawable.ic_battery_charging,R.string.power_charging_summary_text , isOn = false, isShowSwitch = true))
        homeOptionList.add( HomeOption(4,R.string.power_low_text,R.drawable.ic_battery_low,R.string.power_low_summary_text , isOn = false, isShowSwitch = true))
        homeOptionList.add( HomeOption(5,R.string.power_saving_mode_text,R.drawable.ic_battery_saving_mode,R.string.power_saving_mode_summary_text , isOn = false, isShowSwitch = true))



        homeOptionList.add( HomeOption(4,R.string.connected_usb_text,R.drawable.ic_usb,R.string.connected_usb_summary_text , isOn = false, isShowSwitch = true ))
        homeOptionList.add( HomeOption(5,R.string.car_dock_text,R.drawable.ic_car_dock,R.string.car_dock_summary_text , isOn = false, isShowSwitch = true))
        homeOptionList.add( HomeOption(6,R.string.desk_dock_text,R.drawable.ic_desk_dock,R.string.desk_dock_summary_text , isOn = false, isShowSwitch = true ))
        homeOptionList.add( HomeOption(6,R.string.otg_text,R.drawable.ic_otg,R.string.otg_summary_text , isOn = false, isShowSwitch = true ))

        homeOptionList.add( HomeOption(7,R.string.start_on_boot_text,R.drawable.ic_boot,R.string.start_on_boot_summary_text , isOn = false, isShowSwitch = true))
        homeOptionList.add( HomeOption(7,R.string.reboot_devices_text,R.drawable.ic_reboot,R.string.reboot_devices_summary_text , isOn = false, isShowSwitch = true))

        homeOptionList.add( HomeOption(7,R.string.bluetooth_devices_text,R.drawable.ic_bluetooth,R.string.bluetooth_devices_summary_text , isOn = false, isShowSwitch = true))
        homeOptionList.add( HomeOption(7,R.string.nfc_connect_text,R.drawable.ic_nfc,R.string.nfc_connect_summary_text , isOn = false, isShowSwitch = true))
        homeOptionList.add( HomeOption(7,R.string.airplane_mode_text,R.drawable.ic_airplane,R.string.airplane_mode_summary_text , isOn = false, isShowSwitch = true))
        homeOptionList.add( HomeOption(7,R.string.do_not_disturb_mode_text,R.drawable.ic_do_not_distrub,R.string.do_not_disturb_summary_text , isOn = false, isShowSwitch = true))


        homeOptionList.add( HomeOption(9,R.string.display_setting_text,R.drawable.ic_display_setting,R.string.display_setting_summary_text , isOn = false, isShowSwitch = false ))



    }





}