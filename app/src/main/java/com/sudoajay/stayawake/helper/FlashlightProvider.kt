@file:Suppress("DEPRECATION")

package com.sudoajay.stayawake.helper

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build

class FlashlightProvider(private val context: Context) {
    private var mCamera: Camera? = null
    private var parameters: Camera.Parameters? = null
    private var camManager: CameraManager? = null
    fun turnFlashlightOn(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return try {
                camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId: String?
                if (camManager != null) {
                    cameraId = camManager!!.cameraIdList[0]
                    camManager!!.setTorchMode(cameraId, true)
                }
                true
            } catch (e: Exception) {
                false
            }
        } else {
            mCamera = Camera.open()
            parameters = mCamera!!.parameters
            parameters!!.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            mCamera!!.parameters = parameters
            mCamera!!.startPreview()
            return true
        }
    }

    fun turnFlashlightOff(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return try {
                val cameraId: String
                camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                if (camManager != null) {
                    cameraId =
                        camManager!!.cameraIdList[0] // Usually front camera is at 0 position.
                    camManager!!.setTorchMode(cameraId, false)
                }
                false
            } catch (e: Exception) {
                true
            }
        } else {
            mCamera = Camera.open()
            parameters = mCamera!!.parameters
            parameters!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
            mCamera!!.parameters = parameters
            mCamera!!.stopPreview()
            return false
        }
    }

    fun flashStatus(): Boolean {
        mCamera = Camera.open()
        val parameters: Camera.Parameters = mCamera!!.parameters
        if (parameters.flashMode === "FLASH_MODE_TORCH") {
            return true
        }
        if (parameters.flashMode === "FLASH_MODE_OFF") {
            return false
        }
        return false
    }

    companion object {
        private val TAG = FlashlightProvider::class.java.simpleName
    }


}