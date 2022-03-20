package com.example.jhkim.data.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

abstract class BaseDataSource {

    fun <T> getResult(
        call: Call<T>,
        callBack: (T) -> Unit
    ) {
        call.enqueue(object : Callback<T> {

            override fun onResponse(call: Call<T>, response: Response<T>) {

                if (response.isSuccessful) {


                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }


//    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
//        try {
//            val response = call()
//            if (response.isSuccessful) {
//                val body = response.body()
//                if (body != null) return Resource.success(body)
//            }
//            return error(" ${response.code()} ${response.message()}")
//        } catch (e: Exception) {
//            return error(e.message ?: e.toString())
//        }
//    }
//
//    private fun <T> error(message: String): Resource<T> {
//        Timber.d(message)
//        return Resource.error(message)
//    }





}