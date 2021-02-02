package com.sork.common.extension

import android.view.HapticFeedbackConstants
import android.view.View

fun View.setOnClickListenerWithHaptic(onClick: (View) -> Unit) {
    isHapticFeedbackEnabled = true
    setOnClickListener {
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
        onClick(it)
    }
}