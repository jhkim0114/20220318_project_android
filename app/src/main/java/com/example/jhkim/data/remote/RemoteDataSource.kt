package com.example.jhkim.data.remote

import com.example.jhkim.data.entities.Image
import com.example.jhkim.data.entities.Remote
import com.example.jhkim.data.entities.ResponseData
import com.example.jhkim.data.entities.Vclip
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val thumbnailService: ThumbnailService
) : BaseDataSource() {

    enum class Sort(
        val value: String,
    ) {
        ACCURACY("accuracy"),
        RECENCY("recency"),
    }

    fun getImageData(text: String, page: Int, result: (Remote<ResponseData<Image>>) -> Unit) =
        apiRequest (
            thumbnailService.getImage(text, Sort.RECENCY.value, page), result
        )

    fun getVclipData(text: String, page: Int, result: (Remote<ResponseData<Vclip>>) -> Unit) =
        apiRequest (
            thumbnailService.getVclip(text, Sort.RECENCY.value, page), result
        )

}