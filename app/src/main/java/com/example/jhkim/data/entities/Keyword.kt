package com.example.jhkim.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Keyword(
    var text: String = "",
    var image_is_end: Boolean = false,
    var vclip_is_end: Boolean = false,
    var image_page: Int = 1,
    var vclip_page: Int = 1,
    var search_date: Long = Calendar.getInstance().timeInMillis
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}


