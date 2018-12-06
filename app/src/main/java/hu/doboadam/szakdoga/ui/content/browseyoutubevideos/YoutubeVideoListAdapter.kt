package hu.doboadam.szakdoga.ui.content.browseyoutubevideos

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import hu.doboadam.szakdoga.R
import hu.doboadam.szakdoga.extensions.getMostFittingThumbnailUrl
import hu.doboadam.szakdoga.model.YoutubeVideoOnlySnippet
import kotlinx.android.synthetic.main.item_video.view.*

class YoutubeVideoListAdapter(private val videos: MutableList<YoutubeVideoOnlySnippet>, private val listener: (YoutubeVideoOnlySnippet) -> Unit) : RecyclerView.Adapter<YoutubeVideoListAdapter.YoutubeVideoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeVideoViewHolder {
        return YoutubeVideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: YoutubeVideoViewHolder, position: Int) {
        holder.bind(videos[position], listener)
    }

    fun setList(list: List<YoutubeVideoOnlySnippet>) {
        videos.clear()
        videos.addAll(list.filter { it.id.kind == "youtube#video"} )
        notifyDataSetChanged()
    }


    class YoutubeVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(youtubeVideo: YoutubeVideoOnlySnippet, listener: (YoutubeVideoOnlySnippet) -> Unit) = with(itemView) {
            with(youtubeVideo) {
                Picasso.get()
                        .load(snippet.thumbnails.getMostFittingThumbnailUrl())
                        .placeholder(R.drawable.video_placeholder)
                        .into(videoThumbnail)
                videoTitle.text = snippet.title
                videoDuration.visibility = View.GONE
            }
            setOnClickListener { listener(youtubeVideo) }
        }
    }
}