package hu.doboadam.howtube.extensions

import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import hu.doboadam.howtube.ui.BaseViewModelFragment


fun AppCompatActivity.createDialog(func: AlertDialog.Builder.() -> Unit): AlertDialog =
        AlertDialog.Builder(this).apply {
            func()
        }.create()

fun Fragment.createDialog(func: AlertDialog.Builder.() -> Unit): AlertDialog? =
        context?.let {
            AlertDialog.Builder(it).apply {
                func()
            }.create()
        }

fun AppCompatActivity.replaceFragment(fragment: BaseViewModelFragment, containerId: Int, withBackStack: Boolean) {
    val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(containerId, fragment)
    if (withBackStack) {
        transaction.addToBackStack(fragment.TAG)
    }
    transaction.commit()
}

fun Fragment.replaceFragment(fragment: BaseViewModelFragment, containerId: Int, withBackStack: Boolean) {
    val transaction = childFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(containerId, fragment)
    if (withBackStack) {
        transaction.addToBackStack(fragment.TAG)
    }
    transaction.commit()
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
