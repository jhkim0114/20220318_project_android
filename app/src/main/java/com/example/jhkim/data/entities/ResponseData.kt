package com.example.jhkim.data.entities

import com.google.gson.annotations.SerializedName

data class ResponseData<T>(@SerializedName("documents") val documents: T, @SerializedName("meta") val meta: Meta)
