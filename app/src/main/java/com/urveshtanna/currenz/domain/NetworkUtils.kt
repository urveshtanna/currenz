package com.urveshtanna.currenz.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkUtils(var context: Context) {

    var networkType = -1

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return run {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null) {
                networkType = networkInfo.type
            }
            if (networkInfo == null) {
                return false
            }
            val network = networkInfo.state
            network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING
        }
    }

    fun isWIFI(): Boolean {
        var isWIFI = false
        if (networkType == ConnectivityManager.TYPE_WIFI) {
            isWIFI = true
        }
        return isWIFI
    }

    fun isMobile(): Boolean {
        var isMobile = false
        if (networkType == ConnectivityManager.TYPE_MOBILE) {
            isMobile = true
        }
        return isMobile
    }

    fun isRoaming(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return run {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isRoaming
        }
    }
}