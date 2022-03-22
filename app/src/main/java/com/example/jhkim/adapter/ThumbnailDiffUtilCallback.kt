package com.example.jhkim.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.jhkim.data.entities.Thumbnail

class ThumbnailDiffUtilCallback : DiffUtil.ItemCallback<Thumbnail>() {

    override fun areItemsTheSame(oldItem: Thumbnail, newItem: Thumbnail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Thumbnail, newItem: Thumbnail): Boolean {
        return oldItem.id == newItem.id && oldItem.isLike == newItem.isLike
    }
}