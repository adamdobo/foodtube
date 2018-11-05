package hu.doboadam.howtube.ui.content.categories

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.howtube.R
import hu.doboadam.howtube.model.Category
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(private val categoryList: MutableList<Category>, private val listener: (Category) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.CategoryViewHolder {
        return CategoryAdapter.CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position], listener)
    }

    fun setItems(it: List<Category>) {
        categoryList.clear()
        categoryList.addAll(it)
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category, listener: (Category) -> Unit) = with(itemView) {
            categoryName.text = category.name
            itemView.setOnClickListener {
                listener(category)
            }
            when(category.name){
                "Desserts" -> categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dessert))
                "Soups" -> categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.soup))
                "Meatlover" -> categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.meatlover))
                "Vegetarian" -> categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.vegetarian))
                "Pasta" -> categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pasta))
            }
        }

    }
}