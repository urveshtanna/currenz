package com.urveshtanna.currenz.domain.dataModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
open class BaseResponse() : Parcelable {

    @SerializedName("success")
    var success: Boolean? = false

    @SerializedName("terms")
    var terms: String? = null

    @SerializedName("privacy")
    var privacy: String? = null

    @SerializedName("error")
    var errorModel: ErrorModel? = null

}