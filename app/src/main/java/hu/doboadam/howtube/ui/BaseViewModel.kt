package hu.doboadam.howtube.ui

import android.arch.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

abstract class BaseViewModel: ViewModel() {

    protected val db = FirebaseFirestore.getInstance()
    protected lateinit var listener: ListenerRegistration


    abstract fun startListeningToDbChanges()

    fun stopListeningToDbChanges(){
        listener.remove()
    }

}