package com.urveshtanna.currenz.domain.dataModel

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class AvailableCurrencyResponse : BaseResponse() {

    @SerializedName("currencies")
    var currencies: HashMap<String, String>? = null

}