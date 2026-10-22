package com.example.remainderx.util

import android.view.HapticFeedbackConstants
import android.view.View

object HapticHelper {

    fun performClick(view: View, enabled: Boolean) {
        if (!enabled) return
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun performSuccess(view: View, enabled: Boolean) {
        if (!enabled) return
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    fun performError(view: View, enabled: Boolean) {
        if (!enabled) return
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }
}
