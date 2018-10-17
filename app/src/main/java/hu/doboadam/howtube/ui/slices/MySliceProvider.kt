package hu.doboadam.howtube.ui.slices

import android.annotation.SuppressLint
import android.net.Uri
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder

class MySliceProvider : SliceProvider() {


    override fun onBindSlice(sliceUri: Uri?): Slice? {
        when (sliceUri?.path) {
            "/videos" -> createVideosSlice(sliceUri)
        }
        return null
    }

    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    @SuppressLint("Slices", "RestrictedApi")
    private fun createVideosSlice(sliceUri: Uri): androidx.slice.Slice {
        val listBuilder = ListBuilder(context!!, sliceUri, ListBuilder.INFINITY)

        val rowBuilder = ListBuilder.RowBuilder(listBuilder)
        rowBuilder.title = "Videos"
        rowBuilder.endItems.addAll(listOf("Egy", "Kettő", "Három"))

        return listBuilder.build()
    }
}