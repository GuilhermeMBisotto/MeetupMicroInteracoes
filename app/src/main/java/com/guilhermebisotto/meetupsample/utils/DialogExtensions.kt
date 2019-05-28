package com.guilhermebisotto.meetupsample.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.guilhermebisotto.meetupsample.view.customview.AnimationDialog

inline fun Activity.showAnimationDialog(func: AnimationDialog.() -> Unit): AlertDialog =
    AnimationDialog(this).apply {
        func()
    }.create()