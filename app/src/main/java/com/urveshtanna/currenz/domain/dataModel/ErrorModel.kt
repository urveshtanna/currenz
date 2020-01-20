package com.urveshtanna.currenz.domain.dataModel

import com.google.gson.annotations.SerializedName

class ErrorModel(@SerializedName("code") var code: String = "-1",
                 @SerializedName("info") var message: String = "Something went wrong",
                 @SerializedName("type") var type: String? = null) {

}