package com.example.jhkim.data.remote

import com.example.jhkim.data.entities.Meta
import com.google.gson.annotations.SerializedName
import javax.inject.Inject

data class ResponseData<T>(@SerializedName("documents") val documents: T, @SerializedName("meta") val meta: Meta)

class RemoteDataSource @Inject constructor(
    private val thumbnailService: ThumbnailService
) : BaseDataSource() {

    fun getSearchData(text: String) = thumbnailService.getImage(text, "recency")

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