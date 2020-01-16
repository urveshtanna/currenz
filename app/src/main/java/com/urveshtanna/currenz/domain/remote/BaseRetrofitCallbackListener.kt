package com.urveshtanna.currenz.domain.remote

import android.app.Activity
import com.google.gson.Gson
import com.urveshtanna.currenz.domain.dataModel.ErrorModel
import com.urveshtanna.currenz.ui.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

abstract class BaseRetrofitCallbackListener<T>(var activity: Activity) : Callback<T> {

    var errorModel: ErrorModel = ErrorModel()

    abstract fun onAPIResponse(call: Call<T>, response: Response<T>)

    abstract fun onAPIFailure(call: Call<T>, throwable: Throwable)

    abstract fun onAPIError(call: Call<T>, errorModel: ErrorModel)

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onAPIResponse(call, response)
        } else {
            var errorString = ""
            if (response.errorBody() != null) {
                val responseBody = response.errorBody()
                errorString = responseBody?.string()!!
            }
            try {
                errorModel = Gson().fromJson(errorString, ErrorModel::class.java);
            } catch (e: Exception) {
                errorModel = ErrorModel()
            }

            onAPIError(call, errorModel)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        t.printStackTrace()
        if (t is UnknownHostException || t is ConnectException || t is SocketTimeoutException || t is SSLException) {
            Utils.showInternetErrorPopup(
                activity,
                object : Utils.Companion.OnNetworkRetryListener {
                    override fun onRetry() {
                        call.clone().enqueue(this@BaseRetrofitCallbackListener)
                    }

                })
        } else {
            onAPIFailure(call, t)
        }
    }

}