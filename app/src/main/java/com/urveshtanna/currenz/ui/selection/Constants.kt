package com.urveshtanna.currenz.ui.selection

import com.urveshtanna.currenz.SettingsUtils

class Constants {

    companion object {

        //30 min to refresh the rates if debug then 1 min
        val REFRESH_TIME_TO_FETCH_LIVE_RATE: Long = if (SettingsUtils.isDebug()) {
            1 * 60000
        } else {
            30 * 60000
        }

        //Default user preferred currency
        val DEFAULT_CURRENCY = "USD"

        //Default source as free plan of https://currencylayer.com default is USD
        val DEFAULT_SOURCE = "USD"

    }
}