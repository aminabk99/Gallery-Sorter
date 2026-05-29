package com.example.project4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project4.databinding.ItemVideoBinding

/**
 * RecyclerView adapter for the vertical media feed.
 * Displays photos with an ImageView and videos with a VideoView.
 */
class MediaPagerAdapter(
    private var items: MutableList<MediaItem>,
    private val onProfileTap: () -> Unit
) : RecyclerView.Adapter<MediaPagerAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: MediaViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<MediaItem>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): MediaItem = items[position]

    fun removeAt(position: Int): MediaItem {
        val removed = items.removeAt(position)
        notifyItemRemoved(position)
        return removed
    }

    fun isEmpty(): Boolean = items.isEmpty()

    inner class MediaViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MediaItem) {
            binding.tvProfile.text = "Profile"
            binding.tvVideoTitle.text = item.name
            binding.tvProfile.setOnClickListener { onProfileTap() }

            if (item.isVideo) {
                binding.imageView.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
                binding.videoView.setVideoURI(item.uri)
                binding.videoView.setOnPreparedListener { it.isLooping = true }
                binding.videoView.start()
            } else {
                binding.videoView.stopPlayback()
                binding.videoView.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                binding.imageView.setImageURI(item.uri)
            }
        }

        /** Releases media resources when the ViewHolder is recycled. */
        fun recycle() {
            binding.videoView.stopPlayback()
            binding.imageView.setImageDrawable(null)
        }
    }
}
