package com.urveshtanna.currenz.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkUtils(var context: Context) {

    var networkType = -1

    /**
     * This function return is user devices has available internet connection
     */
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
}