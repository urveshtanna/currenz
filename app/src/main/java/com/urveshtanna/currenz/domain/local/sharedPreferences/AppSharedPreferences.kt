package com.urveshtanna.currenz.domain.local.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.urveshtanna.currenz.ui.selection.Constants

class AppSharedPreferences(context: Context) {

    companion object{
        var SHARED_STORAGE_KEY = "app_settings"
        var USER_SYMBOL_STORAGE_KEY = "preferred_currency"
    }

    private var sharedPreferences: SharedPreferences? =
        context.getSharedPreferences(SHARED_STORAGE_KEY, Context.MODE_PRIVATE)

    /**
     * This function return's last selection symbol by user saved in shared_preference
     * @return user's selected symbol. Default is USD
     */
    fun getUserPreferredCurrency(): String? {
        return sharedPreferences?.getString(USER_SYMBOL_STORAGE_KEY, Constants.DEFAULT_CURRENCY)
    }

    /**
     * This function is used to save preferred currency in shared_preference
     * @param currency value to be saved
     */
    fun setUserPreferredCurrency(currency: String?) {
        val editor = sharedPreferences!!.edit()
        editor.putString(USER_SYMBOL_STORAGE_KEY, currency)
        editor.apply()
    }

}