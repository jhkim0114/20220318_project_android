package com.example.jhkim.data.remote

import com.example.jhkim.data.entities.Image
import com.example.jhkim.data.entities.Meta
import com.example.jhkim.data.entities.ResponseData
import com.example.jhkim.data.entities.Vclip
import com.google.gson.annotations.SerializedName
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val thumbnailService: ThumbnailService
) : BaseDataSource() {

    fun getImageData(text: String, page: Int, result: ((ResponseData<List<Image>>)->Unit)) = apiRequest (
        thumbnailService.getImage(text, "recency", page), result
    )

    fun getVclipData(text: String, page: Int, result: ((ResponseData<List<Vclip>>)->Unit)) = apiRequest (
        thumbnailService.getVclip(text, "recency", page), result
    )

//    fun getSearchData(text: String) {
//        thumbnailService.getImage(text)
////        thumbnailService.getVclip(text)
//    }

//    fun getSearchData(text: String) = getResult {
//        thumbnailService.getImage(text)
//    }

    fun getSearchData1(text: String) {
//        val call = thumbnailService.getImage(text)
//        call.enqueue(object: Callback<ResponseData<List<Image>>> {
//
//            override fun onResponse(
//                call: Call<List<Image>>,
//                response: Response<List<Image>>
//            ) {
//                try {
//                    if (response.isSuccessful) {
//                        val body = response.body()
//                        if (body != null) {
//                            Timber.d(response.toString())
//                        }
//                    }
//                    return error(" ${response.code()} ${response.message()}")
//                } catch (e: Exception) {
//                    return error(e.message ?: e.toString())
//                }
//
//
//            }
//
//            override fun onFailure(call: Call<List<Image>>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }


}