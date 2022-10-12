package com.sudoajay.stayawake.utill

import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter("app:setVisibility")
fun setVisibility(view: View, v: Boolean?) {
    view.show(v == true)
}