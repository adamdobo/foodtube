package hu.doboadam.howtube.extensions

import android.app.Activity
import android.app.AlertDialog


fun Activity.createDialog(func: AlertDialog.Builder.() -> Unit): AlertDialog =
        AlertDialog.Builder(this).apply {
            func()
        }.create()