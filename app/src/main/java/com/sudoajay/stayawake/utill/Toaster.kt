package com.sudoajay.stayawake.utill

import android.content.Context
import android.widget.Toast

object Toaster {
    fun showToast(context: Context ,  message:String){
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}