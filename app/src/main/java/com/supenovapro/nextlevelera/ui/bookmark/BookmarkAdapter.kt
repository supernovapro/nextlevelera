package com.supenovapro.nextlevelera.ui.bookmark

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.MaterialToolbar
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.data.TrendNewsBookmark
import com.supenovapro.nextlevelera.databinding.ItemBookmarkArticleBinding

class BookmarkAdapter(private val listener: OnItemClickListener) :
    ListAdapter<TrendNewsBookmark, BookmarkAdapter.BookmarkViewHolder>(
        BOOKMARK_COMPARATOR
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding =
            ItemBookmarkArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val currentBookmark = getItem(position)
        if (currentBookmark != null) {
            holder.bind(currentBookmark)
        }
    }

    inner class BookmarkViewHolder(private val binding: ItemBookmarkArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val bookmark = getItem(position)
                        listener.onOpenBookmarkClick(bookmark)
                    }
                }
                bookmarkRemove.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val bookmark = getItem(position)
                        listener.onDeleteClick(bookmark)
                    }
                }

            }
        }

        fun bind(trendBookmark: TrendNewsBookmark) {
          //  val color = Color.BLUE
           // val htmlText = Html.fromHtml("<font color=\"$color\">${trendBookmark.source}</font>",0)
            binding.apply {

                Glide.with(itemView)
                    .load(trendBookmark.imageUrl)
                    //.centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_news)
                    .into(bookmarkNewsImage)
                bookmarkTime.text = trendBookmark.newsContent
                bookmarkLongTitle.text = cleanString(trendBookmark.title).trim()

                bookmarkShortTitle.text = trendBookmark.source ?: "source Trend News"
            }
        }

    }
    interface OnItemClickListener {
        fun onOpenBookmarkClick(bookmark: TrendNewsBookmark)
        fun onDeleteClick(bookmark: TrendNewsBookmark)
    }

    companion object {
        private val BOOKMARK_COMPARATOR = object : DiffUtil.ItemCallback<TrendNewsBookmark>() {
            override fun areItemsTheSame(
                oldItem: TrendNewsBookmark,
                newItem: TrendNewsBookmark
            ): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: TrendNewsBookmark,
                newItem: TrendNewsBookmark
            ): Boolean =
                oldItem == newItem
        }
    }



    private fun cleanString(string: String): String {
        return string.replace("\n", "").replace("\t", "").replace("  ", " ")

    }

}