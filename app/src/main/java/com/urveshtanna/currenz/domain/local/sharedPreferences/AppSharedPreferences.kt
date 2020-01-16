package com.urveshtanna.currenz.domain.local.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.urveshtanna.currenz.ui.selection.Constants

class AppSharedPreferences(var context: Context) {

    var sharedPreferences: SharedPreferences? =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)


    fun getUserPreferredCurrency(): String? {
        return sharedPreferences?.getString("preferred_currency", Constants.DEFAULT_CURRENCY)
    }

    fun setUserPreferredCurrency(currency: String?) {
        val editor = sharedPreferences!!.edit()
        editor.putString("preferred_currency", currency)
        editor.apply()
    }

}