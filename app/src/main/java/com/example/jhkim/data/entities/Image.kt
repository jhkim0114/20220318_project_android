package com.example.jhkim.data.entities

import java.util.*

data class Image(
    val collection: String,
    val datetime: String,
    val display_sitename: String,
    val doc_url: String,
    val height: Int,
    val image_url: String,
    val thumbnail_url: String,
    val width: Int
)

/*
    "collection": "news",
    "datetime": "2022-02-28T16:40:57.000+09:00",
    "display_sitename": "지디넷코리아",
    "doc_url": "http://v.media.daum.net/v/20220228164057713",
    "height": 426,
    "image_url": "https://t1.daumcdn.net/news/202202/28/ZDNetKorea/20220228164100964velj.jpg",
    "thumbnail_url": "https://search4.kakaocdn.net/argon/130x130_85_c/auhjkF7mn9",
    "width": 639
 */
