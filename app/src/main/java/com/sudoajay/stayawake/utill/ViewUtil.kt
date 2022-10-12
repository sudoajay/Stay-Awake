package com.sudoajay.stayawake.utill

import android.view.View


fun View?.show(show: Boolean) {
    this?.let {
        visibility = if (show) View.VISIBLE else View.GONE
    }
}