package com.sork.common.util

import android.content.res.Resources

object ViewUtil {
    fun dpToPx(dp: Double): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Double): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }
}