package com.urveshtanna.currenz.domain.dataModel

import com.google.gson.annotations.SerializedName

open class BaseResponse {

    @SerializedName("success")
    var success: Boolean? = false

    @SerializedName("terms")
    var terms: String? = null

    @SerializedName("privacy")
    var privacy: String? = null

    @SerializedName("error")
    var errorModel: ErrorModel? = null

}