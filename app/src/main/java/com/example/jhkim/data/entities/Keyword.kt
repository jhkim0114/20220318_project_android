package com.example.jhkim.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Keyword(
    @ColumnInfo(name = "text") var text: String = "",
    @ColumnInfo(name = "image_is_end") var imageIsEnd: Boolean = false,
    @ColumnInfo(name = "vclip_is_end") var vclipIsEnd: Boolean = false,
    @ColumnInfo(name = "image_page") var imagePage: Int = 1,
    @ColumnInfo(name = "vclip_page") var vclipPage: Int = 1,
    @ColumnInfo(name = "search_date") var searchDate: Long = Calendar.getInstance().timeInMillis
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}