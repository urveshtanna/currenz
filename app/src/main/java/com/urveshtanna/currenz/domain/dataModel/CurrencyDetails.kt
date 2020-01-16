package com.urveshtanna.currenz.domain.dataModel

class CurrencyDetails {

    var symbol: String? = null

    var name: String? = null

    fun formattedName(): String {
        return "$name / $symbol"
    }
}