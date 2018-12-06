package hu.doboadam.szakdoga.ui.content

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import hu.doboadam.szakdoga.model.Contest
import hu.doboadam.szakdoga.model.FirestoreRepository
import hu.doboadam.szakdoga.ui.BaseViewModel
import timber.log.Timber

class ContentViewModel: BaseViewModel() {

    private val contestLiveData: MutableLiveData<Contest> = MutableLiveData()

    val getContestLiveData: LiveData<Contest> = contestLiveData

    override fun startListeningToDbChanges() {
        super.startListeningToDbChanges()
        listener = FirestoreRepository.listenToCollectionChanges("contest") { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (firebaseFirestoreException != null) {
                Timber.e("Listening failed with $firebaseFirestoreException")
                return@listenToCollectionChanges
            }
            if (!querySnapshot?.isEmpty!!) {
                contestLiveData.postValue(querySnapshot.documents[0].toObject(Contest::class.java))
            }
        }
    }

}