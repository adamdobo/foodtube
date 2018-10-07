package hu.doboadam.howtube.extensions

import android.app.AlertDialog
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


fun AppCompatActivity.createDialog(func: AlertDialog.Builder.() -> Unit): AlertDialog =
        AlertDialog.Builder(this).apply {
            func()
        }.create()

fun Fragment.createDialog(func: AlertDialog.Builder.() -> Unit): AlertDialog =
        AlertDialog.Builder(context).apply {
            func()
        }.create()

fun AppCompatActivity.replaceFragment(fragment: Fragment, containerId: Int) {
    supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(containerId, fragment)
            .commit()
}

fun AppCompatActivity.addFragmentWithTag(fragment: Fragment, containerId: Int, tag: String) {
    supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .add(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    supportFragmentManager.executePendingTransactions()
}

fun AppCompatActivity.getFirebaseUserId() =
        FirebaseAuth.getInstance().currentUser?.uid
