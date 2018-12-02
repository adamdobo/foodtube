package hu.doboadam.szakdoga.ui.playvideo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.szakdoga.R
import hu.doboadam.szakdoga.model.Comment
import kotlinx.android.synthetic.main.item_comment.view.*
import java.text.SimpleDateFormat
import java.util.*

class CommentListAdapter(private val commentList: MutableList<Comment>) : RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))
    }

    override fun getItemCount() = commentList.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    fun refreshItems(comments: List<Comment>) {
        commentList.clear()
        commentList.addAll(comments)
        commentList.sortByDescending { it.timeStamp }
        notifyDataSetChanged()
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comment: Comment) = with(itemView) {
            with(comment){
                commentMessage.text = message
                commentAuthor.text = author
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                commentTime.text = sdf.format(Date(timeStamp))
            }
        }

    }
}

