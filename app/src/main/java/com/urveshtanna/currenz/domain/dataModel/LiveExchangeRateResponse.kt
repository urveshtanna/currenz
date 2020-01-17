package com.urveshtanna.currenz.domain.dataModel

import com.google.gson.annotations.SerializedName

class LiveExchangeRateResponse : BaseResponse() {

    @SerializedName("quotes")
    var quotes: HashMap<String, Float>? = null

    @SerializedName("timestamp")
    var timestamp: Long? = null

    @SerializedName("source")
    var source: String? = null

}