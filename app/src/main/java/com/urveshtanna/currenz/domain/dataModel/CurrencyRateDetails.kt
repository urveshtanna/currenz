package com.urveshtanna.currenz.domain.dataModel

import com.urveshtanna.currenz.ui.DateFormatter

class CurrencyRateDetails {

    var rate: Double = 0.0

    var symbol: String? = null

    var name: String? = null

    var lastUpdated: Long? = null

    var exchangeValue: Double = 0.0

    fun formattedRate(): String? {
        return "$rate"
    }

    fun formattedExchangeValue(): String? {
        return "$exchangeValue"
    }

    fun formattedLastUpdated(): String? {
        return DateFormatter.formattedTime((lastUpdated!! * 1000), DateFormatter.DATE_FORMAT_DD_MM_YYYY_HH_MM)
    }
}