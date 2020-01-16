package com.urveshtanna.currenz.domain.remote.interfaces.callbacks

import com.urveshtanna.currenz.domain.dataModel.AvailableCurrencyResponse
import com.urveshtanna.currenz.domain.dataModel.ErrorModel
import com.urveshtanna.currenz.domain.dataModel.LiveExchangeRateResponse

class APICallBackListener {

    interface BaseCallBackListener {
        fun onUnexpectedError(apiType: Int, errorMsg: String)
    }

    interface OnProgressListener {
        fun onShowProgressLoader(message: String)
        fun hideProgressLoader()
    }

    interface OnGetAvailableCurrenciesListener : BaseCallBackListener {

        fun onGetAvailableCurrenciesSuccess(availableCurrencyResponse: AvailableCurrencyResponse?)

        fun onGetAvailableCurrenciesError(errorMsg: String, errorModel: ErrorModel)
    }

    interface OnGetLiveExchangeRateListener : BaseCallBackListener {

        fun onGetLiveExchangeRateSuccess(liveExchangeRateResponse: LiveExchangeRateResponse?)

        fun onGetLiveExchangeRateError(errorMsg: String, errorModel: ErrorModel)

    }
}