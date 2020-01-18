package com.urveshtanna.currenz.domain.dataModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class LiveExchangeRateResponse : BaseResponse() {

    @SerializedName("quotes")
    var quotes: HashMap<String, Float>? = null

    @SerializedName("timestamp")
    var timestamp: Long? = null

    @SerializedName("source")
    var source: String? = null

}