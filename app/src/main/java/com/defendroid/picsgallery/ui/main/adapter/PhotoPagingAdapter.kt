/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.defendroid.picsgallery.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.defendroid.picsgallery.R
import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.ui.base.ItemClickListener
import kotlinx.android.synthetic.main.item_photo.view.*

/**
 * A simple adapter implementation that shows Reddit posts.
 */
class PhotoPagingAdapter()
    : PagingDataAdapter<Photo, PhotoPagingAdapter.DataViewHolder>(POST_COMPARATOR) {

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
            holder: DataViewHolder,
            position: Int,
            payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            holder.updateScore(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PhotoAdapter.DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_photo, parent,
                false
            )
        )

    companion object {
        private val PAYLOAD_SCORE = Any()
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<RedditPost>() {
            override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean =
                    oldItem.name == newItem.name

            override fun getChangePayload(oldItem: RedditPost, newItem: RedditPost): Any? {
                return if (sameExceptScore(oldItem, newItem)) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptScore(oldItem: RedditPost, newItem: RedditPost): Boolean {
            // DON'T do this copy in a real app, it is just convenient here for the demo :)
            // because reddit randomizes scores, we want to pass it as a payload to minimize
            // UI updates between refreshes
            return oldItem.copy(score = newItem.score) == newItem
        }
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: Photo, clickListener: ItemClickListener?) {

            itemView.tv_author_name.text = photo.author

            Glide.with(itemView.iv_photo.context)
                .load(photo.getThumbnailUrl())
                .error(R.drawable.ic_error)
                .into(itemView.iv_photo)

            itemView.setOnClickListener {
                clickListener?.onItemClicked(photo)
            }
        }
    }
}
