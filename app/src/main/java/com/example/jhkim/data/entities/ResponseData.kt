package com.example.jhkim.data.entities

import com.google.gson.annotations.SerializedName

data class ResponseData<T>(@SerializedName("documents") val result: T)