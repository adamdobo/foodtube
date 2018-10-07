package hu.doboadam.howtube.ui.playvideo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.doboadam.howtube.R
import hu.doboadam.howtube.model.Comment
import kotlinx.android.synthetic.main.item_comment.view.*

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
        commentList.sortBy { it.timeStamp }
        notifyDataSetChanged()
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comment: Comment) = with(itemView) {
            with(comment){
                commentMessage.text = message
                commentAuthor.text = author
                commentTime.text = timeStamp.toString()
            }
        }

    }
}

