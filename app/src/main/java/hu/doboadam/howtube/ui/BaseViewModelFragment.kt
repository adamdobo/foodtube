package hu.doboadam.howtube.ui

import android.support.v4.app.Fragment

abstract class BaseViewModelFragment : Fragment() {

    abstract val TAG: String

    override fun onStart() {
        super.onStart()
        startListeningToDb()
    }

    abstract fun startListeningToDb()

    override fun onStop() {
        super.onStop()
        stopListeningToDb()
    }

    abstract fun stopListeningToDb()
}