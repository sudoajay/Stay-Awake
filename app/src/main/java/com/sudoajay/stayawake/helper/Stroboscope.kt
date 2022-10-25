package com.sudoajay.stayawake.helper

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.sudoajay.stayawake.ui.mainActivity.MainActivityViewModel
import kotlinx.coroutines.*

class Stroboscope(var flash: FlashlightProvider, private var viewModel: MainActivityViewModel) {


    private val sosFreq = arrayListOf(
        250L,
        250L,
        250L,
        250L,
        250L,
        250L,
        500L,
        250L,
        500L,
        250L,
        500L,
        250L,
        250L,
        250L,
        250L,
        250L,
        250L,
        1000L
    )
    private var stroboFrequency = 1000L


    fun startCoroutineTimer() = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            runStroboscope()
        }
    }

    private suspend fun runStroboscope() {
        var sosIndex = 0
        while (viewModel.sos.value!!) {
            try {
                flash.turnFlashlightOn()
                val onDuration =
                    if (viewModel.sos.value!!) sosFreq[sosIndex++ % sosFreq.size] else stroboFrequency
                delay(onDuration)
                flash.turnFlashlightOff()
                val offDuration =
                    if (viewModel.sos.value!!) sosFreq[sosIndex++ % sosFreq.size] else stroboFrequency
                delay(offDuration)
            } catch (e: Exception) {
                viewModel.sos.postValue(false)
            }

        }
    }
}