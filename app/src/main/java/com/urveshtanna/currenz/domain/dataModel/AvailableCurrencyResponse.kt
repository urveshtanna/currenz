package com.urveshtanna.currenz.domain.dataModel

import com.google.gson.annotations.SerializedName

class AvailableCurrencyResponse : BaseResponse() {

    @SerializedName("currencies")
    var currencies: HashMap<String, String>? = null

}