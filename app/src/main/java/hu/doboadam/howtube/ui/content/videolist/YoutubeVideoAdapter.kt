package hu.doboadam.howtube.ui.content.videolist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import hu.doboadam.howtube.R
import hu.doboadam.howtube.extensions.getMostFittingThumbnailUrl
import hu.doboadam.howtube.extensions.parseYoutubeDuration
import hu.doboadam.howtube.model.YoutubeVideo
import kotlinx.android.synthetic.main.item_video.view.*
import java.util.*

class YoutubeVideoAdapter(private val videos: MutableList<YoutubeVideo>, private val listener: (YoutubeVideo) -> Unit) : RecyclerView.Adapter<YoutubeVideoAdapter.YoutubeVideoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeVideoViewHolder {
        return YoutubeVideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: YoutubeVideoViewHolder, position: Int) {
        holder.bind(videos[position], listener)
    }

    fun setList(list: List<YoutubeVideo>, startDate: Date?) {
        videos.clear()
        if(startDate != null) {
            videos.addAll(list.filter { it.uploadDate.toDate().time > startDate.time }
                    .sortedByDescending { video -> video.ratings.map { it.rating }.average() })
        } else {
            videos.addAll(list)
        }
        notifyDataSetChanged()
    }


    class YoutubeVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(youtubeVideo: YoutubeVideo, listener: (YoutubeVideo) -> Unit) = with(itemView) {
            with(youtubeVideo) {
            Picasso.get()
                    .load(snippet.thumbnails.getMostFittingThumbnailUrl())
                    .placeholder(R.drawable.video_placeholder)
                    .into(videoThumbnail)
            videoTitle.text = snippet.title
            videoDuration.text = contentDetails.duration.parseYoutubeDuration()
            }
            setOnClickListener { listener(youtubeVideo) }
        }
    }
}

