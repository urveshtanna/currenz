package com.urveshtanna.currenz.domain.remote.repositories

import android.app.Activity
import com.urveshtanna.currenz.domain.remote.interfaces.callbacks.APICallBackListener
import com.urveshtanna.currenz.ui.Utils

open class BaseServerCalls(
    var activity: Activity,
    var onProgressListener: APICallBackListener.OnProgressListener? = null
) {

    var apiType: Int = -1

    fun showProgressLoader(message: String) {
        if (onProgressListener != null)
            onProgressListener?.onShowProgressLoader(message)
        else
            Utils.showProgressLoadingDialog(activity, message)
    }

    fun hideProgressLoader() {
        if (onProgressListener != null)
            onProgressListener?.hideProgressLoader()
        else
            Utils.hideProgressLoadingDialog()
    }
}