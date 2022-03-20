package com.example.jhkim.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.jhkim.R
import com.example.jhkim.data.entities.Thumbnail
import com.example.jhkim.databinding.ItemThumbnailBinding

class SearchAdapter(

) : ListAdapter<Thumbnail, SearchAdapter.ThumbnailViewHolder>(ThumbnailDiffUtilCallback()) {

    private lateinit var binding: ItemThumbnailBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        binding = ItemThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThumbnailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ThumbnailViewHolder(
        private val binding: ItemThumbnailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(thumbnail: Thumbnail) {
            binding.imageViewThumbnail.load(thumbnail.thumbnail_url) {
                crossfade(true)
            }

            binding.textViewDatetime.text = thumbnail.datetime
            when (thumbnail.is_like) {
                true -> binding.buttonLike.setImageResource(R.drawable.ic_baseline_favorite_24)
                false -> binding.buttonLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }

        }

    }

}