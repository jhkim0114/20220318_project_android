package com.example.jhkim.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun String.toLongTime(): Long {
        val datetime = this.substring(0, 10) + " " + this.substring(11, 19)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.parse(datetime).time
    }

    fun Long.toStringTime(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(this)
    }

    fun getCurrentTime(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }

    fun checkNetworkState(context: Context): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val network: Network = connectivityManager.activeNetwork ?: return false
        val actNetwork: NetworkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
        }
    }

}