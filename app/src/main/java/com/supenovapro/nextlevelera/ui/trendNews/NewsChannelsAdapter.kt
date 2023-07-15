package com.supenovapro.nextlevelera.ui.trendNews

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.supenovapro.nextlevelera.data.NewsWebsite
import com.supenovapro.nextlevelera.databinding.ItemNewsChannelBinding

class NewsChannelsAdapter(private val listener: OnItemClickListener) : ListAdapter<NewsWebsite,
        NewsChannelsAdapter.NewsChannelsViewHolder>(CHANNELS_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsChannelsViewHolder {
        val binding =
            ItemNewsChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NewsChannelsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsChannelsViewHolder, position: Int) {
        val currentChannel = getItem(position)
        if (currentChannel != null) {
            holder.bind(currentChannel)
        }
    }

    inner class NewsChannelsViewHolder(private val binding: ItemNewsChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val channel = getItem(position)
                        listener.onChannelClick(channel)
                    }
                }
            }
        }

        fun bind(channel: NewsWebsite) {
            binding.apply {
                channelNewsIcon.setImageResource(channel.websiteIcon)
                if (channel.name == "The Huffington Post" || channel.name == "Reuters" || channel.name == "The Washington Post"
                    || channel.name == "South China Morning Pos" || channel.name == "Le Monde" || channel.name == "The Sydney Morning Herald"
                    || channel.name == "China Daily" || channel.name == "The Hindu" || channel.name == "Marca" || channel.name == "DW (Deutsche Welle)"
                    || channel.name == "ESPN" || channel.name == "Al Jazeera" || channel.name == "TRT World" || channel.name == "Goal.com"
                    || channel.name == "Fox Sports"
                )
                    channelNewsIcon.scaleType = ImageView.ScaleType.FIT_CENTER
                else
                    channelNewsIcon.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

    }

    interface OnItemClickListener {
        fun onChannelClick(channel: NewsWebsite)
    }

    companion object {
        private val CHANNELS_COMPARATOR = object : DiffUtil.ItemCallback<NewsWebsite>() {
            override fun areItemsTheSame(oldItem: NewsWebsite, newItem: NewsWebsite): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: NewsWebsite, newItem: NewsWebsite): Boolean =
                oldItem == newItem
        }
    }


}
