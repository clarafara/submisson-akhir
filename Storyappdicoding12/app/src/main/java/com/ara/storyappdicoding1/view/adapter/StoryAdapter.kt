package com.ara.storyappdicoding1.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ara.storyappdicoding1.R
import com.ara.storyappdicoding1.data.remote.response.ListStoryItem
import com.ara.storyappdicoding1.databinding.ItemRowBinding
import com.ara.storyappdicoding1.view.detail.DetailActivity
import com.ara.storyappdicoding1.view.detail.DetailActivity.Companion.EXTRA_STORY
import com.ara.storyappdicoding1.view.utils.DateFormatter
import com.bumptech.glide.Glide
import java.util.TimeZone


class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.ivItemPhoto.load(data.photoUrl) {
                placeholder(R.color.dim)
            }
            binding.tvItemName.text = data.name
            binding.tvItemDescription.text = data.description
            binding.tvDate.text = DateFormatter.formatDate(data.createdAt, TimeZone.getDefault().id)

            binding.cv.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "photo"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemDescription, "description"),
                        Pair(binding.tvDate,"createdAt")
                    )

                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(EXTRA_STORY, data)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = getItem(position)
        if (list != null) {
            holder.bind(list)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}