package com.urveshtanna.currenz.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.urveshtanna.currenz.R
import com.urveshtanna.currenz.databinding.DialogCustomProgressLoaderBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type


class Utils() {

    companion object {

        var dialog: AlertDialog? = null
        var progressDialog: AlertDialog? = null
        val MESSAGE_TYPE_ERROR = 1
        val MESSAGE_TYPE_NORMAL = 0

        interface OnSnackBarActionListener {
            fun onClick()
        }

        interface OnNetworkRetryListener {
            fun onRetry()
        }

        interface OnErrorMessageDialogListener {
            fun onPositiveActionClick(dialog: DialogInterface)

            fun onNegativeActionClick(dialog: DialogInterface)
        }


        fun hideKeyboard(activity: Activity) {
            try {
                val inputManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // check if no view has focus:
                val v = activity.currentFocus ?: return
                inputManager.hideSoftInputFromWindow(v.windowToken, 0)
            } catch (ex: Exception) {
                Log.getStackTraceString(ex)
            }
        }

        fun showSnackBar(activity: Activity, msg: String, messageType: Int = MESSAGE_TYPE_ERROR) {
            try {
                hideKeyboard(activity)
                val parentView = activity.findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(parentView, msg, Snackbar.LENGTH_LONG)
                if (messageType == MESSAGE_TYPE_ERROR) {
                    snackbar.setBackgroundTint(ContextCompat.getColor(activity, R.color.colorError))
                    snackbar.setTextColor(ContextCompat.getColor(activity, R.color.colorOnError))
                } else {
                    snackbar.setBackgroundTint(
                        ContextCompat.getColor(
                            activity,
                            R.color.backgroundDark
                        )
                    )
                    snackbar.setTextColor(ContextCompat.getColor(activity, R.color.colorOnError))
                }
                snackbar.show()
            } catch (ex: Exception) {
                Log.getStackTraceString(ex)
            }
        }

        fun showToast(activity: Activity, msg: String, duration: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(activity, msg, duration).show()
        }

        fun showSnackbarWithAction(
            activity: Activity,
            view: View?,
            message: String?,
            duration: Int = Snackbar.LENGTH_LONG,
            actionText: String?,
            onSnackBarActionListener: OnSnackBarActionListener
        ) {
            if (view != null) {
                val snackbar = Snackbar.make(view, message!!, duration)
                snackbar.setBackgroundTint(
                    ContextCompat.getColor(
                        view.context,
                        R.color.backgroundDark
                    )
                )
                snackbar.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.primaryInvertedTextColor
                    )
                )
                snackbar.setAction(actionText) { onSnackBarActionListener.onClick() }
                    .setActionTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
                    .show()
            }
        }

        fun showInternetErrorPopup(
            activity: Activity,
            onNetworkRetryListener: OnNetworkRetryListener
        ) {
            if (dialog == null) {
                var alertDialogBuilder = AlertDialog.Builder(activity)
                alertDialogBuilder.setTitle(activity.getString(R.string.app_name))
                alertDialogBuilder.setMessage(activity.getString(R.string.internet_not_available))
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setPositiveButton(activity.getString(R.string.retry)) { _, _ ->
                    onNetworkRetryListener.onRetry()
                }
                dialog = alertDialogBuilder.create()
                dialog!!.show()
            } else {
                dialog!!.dismiss()
                dialog = null
                showInternetErrorPopup(activity, onNetworkRetryListener)
            }
        }

        fun showErrorDialog(
            activity: Activity,
            title: String = activity.getString(R.string.app_name),
            message: String,
            positionButtonText: String,
            negativeButtonText: String? = null,
            onErrorMessageDialogListener: OnErrorMessageDialogListener
        ) {
            if (dialog == null) {
                var alertDialogBuilder = AlertDialog.Builder(activity)
                alertDialogBuilder.setTitle(title)
                alertDialogBuilder.setMessage(message)
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setPositiveButton(positionButtonText) { dialog, _ ->
                    onErrorMessageDialogListener.onPositiveActionClick(dialog)
                }

                if (negativeButtonText != null) {
                    alertDialogBuilder.setNegativeButton(negativeButtonText) { dialog, _ ->
                        onErrorMessageDialogListener.onNegativeActionClick(dialog)
                    }
                }

                dialog = alertDialogBuilder.create()
                dialog!!.show()
            } else {
                dialog!!.dismiss()
                dialog = null
                showErrorDialog(
                    activity,
                    title,
                    message,
                    positionButtonText,
                    negativeButtonText,
                    onErrorMessageDialogListener
                )
            }
        }


        fun showProgressLoadingDialog(
            activity: Activity,
            message: String = activity.getString(R.string.loading)
        ) {
            if (progressDialog == null || progressDialog!!.isShowing) {
                val view: DialogCustomProgressLoaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.dialog_custom_progress_loader,
                    null,
                    false
                )
                view.loadingMessage = message
                val alertDialogBuilder = AlertDialog.Builder(activity)
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setView(view.root)
                progressDialog = alertDialogBuilder.create()
                progressDialog!!.show()
            } else {
                progressDialog!!.dismiss()
                progressDialog = null
                showProgressLoadingDialog(
                    activity,
                    message
                )
            }
        }

        fun hideProgressLoadingDialog() {
            if (progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
                progressDialog = null
            }
        }

        fun mapToString(map: HashMap<String, String>?): String {
            val myMap: String = Gson().toJson(map)
            return myMap
        }

        fun stringToMap(map: String?): HashMap<String, String> {
            val type: Type = object : TypeToken<HashMap<String?, String?>?>() {}.type
            val myMap: HashMap<String, String> = Gson().fromJson(map, type)
            return myMap
        }

        fun showView(vararg views: View?) {
            for (view in views) {
                if (view?.visibility != View.VISIBLE) {
                    val animation: Animation = AlphaAnimation(0.0f, 1.0f)
                    animation.duration = 500
                    view?.startAnimation(animation)
                    view?.visibility = View.VISIBLE
                }
            }
        }

        fun hideView(vararg views: View?) {
             for (view in views) {
                if (view?.visibility == View.VISIBLE) {
                    val animation: Animation = AlphaAnimation(1.0f, 0.0f)
                    animation.duration = 500
                    view.startAnimation(animation)
                    view.visibility = View.GONE
                }
            }
        }

        /**
         * @param is
         * @return String read from the input
         * @throws Exception
         */
        fun convertStreamToString(inputStream: InputStream): String {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            reader.close()
            return sb.toString()
        }

        /***
         *
         * @param context
         * @param filePath
         * @return a mock response that is read from a file with extension
         */
        fun getResponseFromJsonFile(
            context: Context,
            filePath: Int
        ): String? {
            return try {
                val stream = context.resources.openRawResource(filePath)
                val ret: String = convertStreamToString(stream)
                //Make sure you close all streams.
                stream.close()
                ret
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                "{}"
            }
        }
    }

}