package com.example.jhkim.data.remote

import com.example.jhkim.data.entities.Remote
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseDataSource {

    protected fun <T> apiRequest(
        call: Call<T>,
        result: (Remote<T>) -> Unit
    ) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                try {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) result(Remote.success(body))
                        return
                    }
                    onFailure(call, Throwable(message = "${response.code()} ${response.message()}"))
                } catch (e: Exception) {
                    onFailure(call, Throwable(message = e.message ?: e.toString()))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                result(Remote.error("네트워크 호출 실패: ${t.message}"))
            }
        })
    }

}