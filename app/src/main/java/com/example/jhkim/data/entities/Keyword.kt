package com.example.jhkim.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Keyword(
    val text: String,
    val image_is_end: Boolean,
    val vclip_is_end: Boolean,
    val image_page: Int,
    val vclip_page: Int,
    val search_date: Long = Calendar.getInstance().timeInMillis
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}


