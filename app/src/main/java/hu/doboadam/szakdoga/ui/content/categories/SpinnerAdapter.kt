package hu.doboadam.szakdoga.ui.content.categories

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import hu.doboadam.szakdoga.model.Category

class SpinnerAdapter(context: Context, private val textResourceId: Int, private val categories: Array<Category>) : ArrayAdapter<Category>(context, textResourceId, categories) {


    override fun getItem(position: Int): Category? = categories[position]

    override fun getCount(): Int = categories.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val text = super.getView(position, convertView, parent) as TextView

        text.setTextColor(Color.BLACK)
        text.text = categories[position].name
        return text
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val text = super.getDropDownView(position, convertView, parent) as TextView

        text.setTextColor(Color.BLACK)
        text.text = categories[position].name
        return text
    }
}