package com.defendroid.picsgallery.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.defendroid.picsgallery.R
import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.ui.base.ItemClickListener
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoAdapter(
    private val photos: ArrayList<Photo>,
    private val itemClickListener: ItemClickListener?
) : RecyclerView.Adapter<PhotoAdapter.DataViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_photo, parent,
                false
            )
        )

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(photos[position], itemClickListener)
    }

    fun addData(list: List<Photo>) {
        photos.clear()
        photos.addAll(list)
    }
}