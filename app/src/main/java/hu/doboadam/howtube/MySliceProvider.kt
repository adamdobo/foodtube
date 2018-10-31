package hu.doboadam.howtube

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.support.v4.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import com.google.firebase.firestore.FirebaseFirestore
import hu.doboadam.howtube.model.YoutubeVideo
import hu.doboadam.howtube.ui.login.LoginActivity
import hu.doboadam.howtube.ui.playvideo.PlayVideoActivity
import hu.doboadam.howtube.ui.playvideo.PlayVideoActivity.Companion.VIDEO_ID

class MySliceProvider : SliceProvider() {

    private var recipe: YoutubeVideo? = null

    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */

    override fun onCreateSliceProvider(): Boolean {
        return true
    }


    private fun getRandomRecipe() {
        if(recipe == null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("videos").get().addOnSuccessListener {
                recipe = if (it.isEmpty) {
                    null
                } else {
                    it.documents.shuffled()[0].toObject(YoutubeVideo::class.java)
                }
                context?.contentResolver?.notifyChange(Uri.parse("content://hu.doboadam.howtube/recipe"), null)
            }
        }
    }

    /**
     * Converts URL to content URI (i.e. content://hu.doboadam.slices...)
     */
    override fun onMapIntentToUri(intent: Intent?): Uri {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        var uriBuilder: Uri.Builder = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
        if (intent == null) return uriBuilder.build()
        val data = intent.data
        if (data != null && data.path != null) {
            val path = data.path.replace("/", "")
            uriBuilder = uriBuilder.path(path)
        }
        val context = context
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.packageName)
        }
        return uriBuilder.build()
    }

    /**
     * Construct the Slice and bind data if available.
     */
    override fun onBindSlice(sliceUri: Uri): Slice? {
        // Note: you should switch your build.gradle dependency to
        // slice-builders-ktx for a nicer interface in Kotlin.
        val context = context ?: return null
        return if (sliceUri.path == "/recipe") {
            // Path recognized. Customize the Slice using the androidx.slice.builders API.
            // Note: ANR and StrictMode are enforced here so don't do any heavy operations. 
            // Only bind data that is currently available in memory.
            getRandomRecipe()
            val intent = Intent(context, PlayVideoActivity::class.java)
            intent.putExtra(VIDEO_ID, recipe?.id)
            ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                    .addRow {
                        it.title = recipe?.snippet?.title ?: "Loading..."
                        it.primaryAction = SliceAction.create(
                                PendingIntent.getActivity(context, 0, intent, 0),
                                IconCompat.createWithResource(context, android.R.drawable.ic_lock_power_off),
                                ListBuilder.ICON_IMAGE,
                                "Enter app"
                        )
                    }
                    .build()
        } else {
            // Error: Path not found.
            ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                    .addRow {
                        it.title = "URI not found."
                        it.primaryAction = SliceAction.create(
                                PendingIntent.getActivity(context, 0, Intent(context, LoginActivity::class.java), 0),
                                IconCompat.createWithResource(context, android.R.drawable.ic_lock_power_off),
                                ListBuilder.ICON_IMAGE,
                                "Enter app"
                        )
                    }
                    .build()
        }
    }

    private fun createActivityAction(): SliceAction? {
        return null
        /*
        Instead of returning null, you should create a SliceAction. Here is an example:
        return SliceAction.create(
            PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java), 0
            ),
            IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground),
            ListBuilder.ICON_IMAGE,
            "Open App"
        )
        */
    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    override fun onSlicePinned(sliceUri: Uri?) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger MySliceProvider#onBindSlice(Uri) again.
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    override fun onSliceUnpinned(sliceUri: Uri?) {
        // Remove any observers if necessary to avoid memory leaks.
    }
}