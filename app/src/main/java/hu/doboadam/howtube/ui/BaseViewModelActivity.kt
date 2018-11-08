package hu.doboadam.howtube.ui

import android.support.v7.app.AppCompatActivity

abstract class BaseViewModelActivity : AppCompatActivity() {


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