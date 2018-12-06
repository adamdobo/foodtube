package hu.doboadam.szakdoga.model

import com.google.firebase.firestore.*


object FirestoreRepository {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun listenToCollectionChanges(collectionPath: String, run: (querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) -> Unit): ListenerRegistration =
            db.collection(collectionPath).addSnapshotListener { querySnapshot, exception ->
                run(querySnapshot, exception)
            }

    fun <T : Any> addDocumentWithCustomId(documentPath: String, objectToAdd: T) {
        db.document(documentPath).set(objectToAdd)
    }

    fun <T : Any> updateDocumentWithCustomId(documentPath: String, fieldPath: String, updateObject: T) {
        db.document(documentPath).update(fieldPath, updateObject)
    }

    fun listenToDocumentChanges(documentPath: String, run: (documentSnapshot: DocumentSnapshot?, exception: FirebaseFirestoreException?) -> Unit): ListenerRegistration =
            db.document(documentPath).addSnapshotListener { documentSnapshot: DocumentSnapshot?, exception: FirebaseFirestoreException? ->
                run(documentSnapshot, exception)
            }

    fun getCollection(path: String) =
            db.collection(path).get()

    fun getCollectionReference(collectionPath: String) =
            db.collection(collectionPath)
}


