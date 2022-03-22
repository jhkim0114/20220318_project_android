package com.example.jhkim.data.entities

data class Remote<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Type {
        IMAGE,
        VCLIP
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Remote<T> {
            return Remote(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): Remote<T> {
            return Remote(Status.ERROR, data, message)
        }
    }

}