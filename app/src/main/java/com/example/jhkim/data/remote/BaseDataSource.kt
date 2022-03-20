package com.example.jhkim.data.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

abstract class BaseDataSource {

    protected fun <T> apiRequest(
        call: Call<T>,
        result: (T) -> Unit
    ) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                try {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) return result(body)
                    }
                    Timber.d("network error")
                } catch (e: Exception) {
                    Timber.d(e.message)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Timber.d(t.message)
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