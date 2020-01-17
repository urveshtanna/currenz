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

        /**
         * Generic UI function to hide keyboard
         */
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

        /**
         * Generic UI function to show material snackbar
         * @param activity used to call hideKeyboard and also get the root view of activity
         * @param msg message to show in snackbar
         * @param messageType it can be used to show in MESSAGE_TYPE_ERROR with red snackbar
         * or MESSAGE_TYPE_NORMAL which not so important message.
         * Default is MESSAGE_TYPE_ERROR
         */
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

        /**
         * Generic UI function to show material toast
         * @param activity
         * @param msg message to show in snackbar
         * @param duration it can be used to change the duration of toast
         * Default is Toast.LENGTH_SHORT
         */
        fun showToast(activity: Activity, msg: String, duration: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(activity, msg, duration).show()
        }

        /**
         * Generic UI function to show material snackbar with a action button
         * @param activity used to hidekeyboard
         * @param view this view is used to show snackbar or anchor snackbar on that view
         * @param msg message to show in snackbar
         * @param duration it can be used change snackbar duration default is Snackbar.LENGTH_LONG
         * @param actionText text of snackbar action button
         * @param onSnackBarActionListener callback interface on pressing the action button on snackbar
         */
        fun showSnackbarWithAction(
            activity: Activity,
            view: View,
            msg: String,
            duration: Int = Snackbar.LENGTH_LONG,
            actionText: String?,
            onSnackBarActionListener: OnSnackBarActionListener
        ) {
            hideKeyboard(activity)
            val snackbar = Snackbar.make(view, msg, duration)
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

        /**
         * This is a generic function to show network error with a callback to retry.
         * If this dialog is already been shown then old dialog is dismissed and new one is shown
         * @param context is pass the parent context
         * @param onNetworkRetryListener callback interface on pressing retry action
         */
        fun showInternetErrorPopup(
            context: Context,
            onNetworkRetryListener: OnNetworkRetryListener
        ) {
            if (dialog == null) {
                var alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setTitle(context.getString(R.string.app_name))
                alertDialogBuilder.setMessage(context.getString(R.string.internet_not_available))
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setPositiveButton(context.getString(R.string.retry)) { _, _ ->
                    onNetworkRetryListener.onRetry()
                }
                dialog = alertDialogBuilder.create()
                dialog!!.show()
            } else {
                dialog!!.dismiss()
                dialog = null
                showInternetErrorPopup(context, onNetworkRetryListener)
            }
        }

        /**
         * This is a genric function to show errors/success message in dialog. If this dialog is already been shown
         * then if dismisses the old dialog and creates new dialog
         * @param context is used to create the dialog and also get the default strings
         * @param title for the dialog
         * @param msg message that is shown in dialog
         * @param positionButtonText positive button text
         * @param negativeButtonText negative button text, can be null if dialog doesnt need negative action button
         * @param onErrorMessageDialogListener callback interface to handle positive and negative action click
         */
        fun showDialog(
            context: Context,
            title: String = context.getString(R.string.app_name),
            msg: String,
            positionButtonText: String,
            negativeButtonText: String? = null,
            onErrorMessageDialogListener: OnErrorMessageDialogListener
        ) {
            if (dialog == null) {
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setTitle(title)
                alertDialogBuilder.setMessage(msg)
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
                showDialog(
                    context,
                    title,
                    msg,
                    positionButtonText,
                    negativeButtonText,
                    onErrorMessageDialogListener
                )
            }
        }

        /**
         * This UI function used to show a blocking progress loading bar. If this dialog is already
         * been shown then it dismisses old dialog and creates new
         * @param context is used to create dialog & also used to get default string values
         * @param msg message to be shown in loader. Default is R.string.loading
         */
        fun showProgressLoadingDialog(
            context: Context,
            msg: String = context.getString(R.string.loading)
        ) {
            if (progressDialog == null || progressDialog!!.isShowing) {
                val view: DialogCustomProgressLoaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.dialog_custom_progress_loader,
                    null,
                    false
                )
                view.loadingMessage = msg
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setView(view.root)
                progressDialog = alertDialogBuilder.create()
                progressDialog!!.show()
            } else {
                progressDialog!!.dismiss()
                progressDialog = null
                showProgressLoadingDialog(
                    context,
                    msg
                )
            }
        }

        /**
         * This UI function is used to dismisses the progress loading bar
         */
        fun hideProgressLoadingDialog() {
            if (progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
                progressDialog = null
            }
        }

        /**
         * This function converts a hashmap to String
         * @param map hashmap to be converted to string
         * @return string value of hashmap
         */
        fun mapToString(map: HashMap<String, String>?): String {
            val myMap: String = Gson().toJson(map)
            return myMap
        }

        /**
         * This function converts a string to hashmap
         * @param mapString string to be converted to hashmap
         * @return hashmap value of string
         */
        fun stringToMap(mapString: String?): HashMap<String, String> {
            val type: Type = object : TypeToken<HashMap<String?, String?>?>() {}.type
            val myMap: HashMap<String, String> = Gson().fromJson(mapString, type)
            return myMap
        }

        /**
         * This is UI function used to change visibilty of view to VIEW.VISIBLE with a smooth animation
         * @param views list of view to show
         */
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

        /**
         * This is UI function used to change visibilty of view to VIEW.GONE with a smooth animation
         * @param views list of view to hide
         */
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
         * @param filePath path of raw file to fetch
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