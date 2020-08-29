package com.sudoajay.stayawake.activity.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val flashLight: MutableLiveData<Boolean> = MutableLiveData()
    val sos: MutableLiveData<Boolean> = MutableLiveData()
    val displayController: MutableLiveData<Boolean> = MutableLiveData()

    init {
        loadFlashlight()
        loadDisplayController()
        loadSos()
    }

    fun setFlashlight() {
        flashLight.value = !flashLight.value!!
    }
    private fun loadFlashlight() {
        flashLight.value = false
    }

    fun setSos() {
        sos.value = !sos.value!!
    }


    private fun loadSos() {
        sos.value = false
    }

    fun setDisplayController() {
        displayController.value = !displayController.value!!
    }


    private fun loadDisplayController() {
        displayController.value = false
    }
}