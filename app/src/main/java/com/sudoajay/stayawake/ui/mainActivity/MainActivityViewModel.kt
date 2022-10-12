package com.sudoajay.stayawake.ui.mainActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sudoajay.stayawake.helper.FlashlightProvider
import com.sudoajay.stayawake.helper.Stroboscope

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val flashLight: MutableLiveData<Boolean> = MutableLiveData()
    val sos: MutableLiveData<Boolean> = MutableLiveData()
    val displayController: MutableLiveData<Boolean> = MutableLiveData()

    private val _application = application
    private var flash: FlashlightProvider
    private var stro: Stroboscope

    init {
        loadFlashlight()
        loadDisplayController()
        loadSos()
        flash = FlashlightProvider(_application)
        stro = Stroboscope(flash, this)
        stro.startCoroutineTimer()
    }

    fun setFlashlight() {
        flashLight.value = if (flashLight.value!!) {
            flash.turnFlashlightOff()

        } else {
            flash.turnFlashlightOn()
        }
        sos.value = false
        displayController.value = false
    }
    private fun loadFlashlight() {
        flashLight.value = false
    }

    fun setSos() {
        sos.value = !sos.value!!
        flashLight.value = false
        displayController.value = false
    }


    private fun loadSos() {
        sos.value = false
    }


    fun setDisplayController() {
        displayController.value = !displayController.value!!
        flashLight.value = false
        sos.value = false
        flash.turnFlashlightOff()
    }

    fun getDisplayController(): Boolean {
        return displayController.value!!
    }

    private fun loadDisplayController() {
        displayController.value = false
    }

    fun defaultSetting(){
        flashLight.value = false
        sos.value = false
        displayController.value= false
    }

}