package com.example.jhkim.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Thumbnail(
    val type: String,
    val text: String,
    val thumbnail_url: String,
    val datetime: Long,
    val is_like: Boolean = false,
    val like_date: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}


