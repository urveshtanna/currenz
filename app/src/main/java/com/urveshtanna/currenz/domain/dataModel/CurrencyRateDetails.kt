package com.urveshtanna.currenz.domain.dataModel

import com.urveshtanna.currenz.ui.DateFormatter

class CurrencyRateDetails {

    var rate: Float = 0.0f

    var symbol: String? = null

    var name: String? = null

    var lastUpdated: Long? = null

    var exchangeValue: Float = 0.0f

    fun formattedExchangeValue(): String? {
        return exchangeValue.toBigDecimal().toPlainString()
    }

    fun formattedLastUpdated(): String? {
        return DateFormatter.formattedTime((lastUpdated!! * 1000), DateFormatter.DATE_FORMAT_DD_MM_YYYY_HH_MM)
    }
}