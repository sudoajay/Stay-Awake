
package com.sudoajay.stayawake.helper

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import com.sudoajay.stayawake.R

class FlashlightProvider (private val context: Context) {
    private var mCamera: Camera? = null
    private var parameters: Camera.Parameters? = null
    private var camManager: CameraManager? = null
    fun turnFlashlightOn(): Boolean {
        var isFlashOn: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId: String?
                if (camManager != null) {
                    cameraId = camManager!!.cameraIdList[0]
                    camManager!!.setTorchMode(cameraId, true)
                }
                isFlashOn = true
            } catch (e: Exception) {
                isFlashOn = false
            }
        } else {
            mCamera = Camera.open()
            parameters = mCamera!!.parameters
            parameters!!.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            mCamera!!.parameters = parameters
            mCamera!!.startPreview()
            isFlashOn = true
        }
        setValue(isFlashOn)
        return isFlashOn
    }

    fun turnFlashlightOff(): Boolean {
        var isFlashOn: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val cameraId: String
                camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                if (camManager != null) {
                    cameraId =
                        camManager!!.cameraIdList[0] // Usually front camera is at 0 position.
                    camManager!!.setTorchMode(cameraId, false)
                }
                isFlashOn = false
            } catch (e: Exception) {
                isFlashOn = true
            }
        } else {
            mCamera = Camera.open()
            parameters = mCamera!!.parameters
            parameters!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
            mCamera!!.parameters = parameters
            mCamera!!.stopPreview()
            isFlashOn = false
        }
        setValue(isFlashOn)
        return isFlashOn
    }


    private fun setValue(value: Boolean) {
        context.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(context.getString(R.string.is_flash_active_text), value).apply()
    }



}