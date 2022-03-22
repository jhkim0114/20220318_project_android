package com.example.jhkim.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Thumbnail(
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    @ColumnInfo(name = "datetime") val datetime: Long,
    @ColumnInfo(name = "is_like") val isLike: Boolean = false,
    @ColumnInfo(name = "like_date") val likeDate: Long = 0
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}


