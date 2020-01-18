package com.urveshtanna.currenz.domain.remote.repositories

import android.app.Activity
import com.google.gson.Gson
import com.urveshtanna.currenz.R
import com.urveshtanna.currenz.SettingsUtils
import com.urveshtanna.currenz.domain.NetworkUtils
import com.urveshtanna.currenz.domain.dataModel.AvailableCurrencyResponse
import com.urveshtanna.currenz.domain.dataModel.ErrorModel
import com.urveshtanna.currenz.domain.dataModel.LiveExchangeRateResponse
import com.urveshtanna.currenz.domain.remote.BaseRetrofitCallbackListener
import com.urveshtanna.currenz.domain.remote.adapters.CurrencyConversionServiceAdapter
import com.urveshtanna.currenz.domain.remote.interfaces.callbacks.APICallBackListener
import com.urveshtanna.currenz.ui.Utils
import retrofit2.Call
import retrofit2.Response

class CurrencyConversionServerCalls(
    activity: Activity,
    onProgressListener: APICallBackListener.OnProgressListener? = null
) : BaseServerCalls(activity, onProgressListener) {

    fun getAvailableCurrencies(
        accessKey: String,
        onGetAvailableCurrenciesListener: APICallBackListener.OnGetAvailableCurrenciesListener
    ) {
        if (!SettingsUtils.makeMockAPICalls()) {
            if (NetworkUtils(activity).isNetworkAvailable()) {
                val call = CurrencyConversionServiceAdapter(activity).getCurrencyConversionAdapter()
                    ?.getAllAvailableCurrencies(accessKey)

                showProgressLoader(activity.getString(R.string.fetching_available_currencies))
                call?.enqueue(object :
                    BaseRetrofitCallbackListener<AvailableCurrencyResponse>(activity) {
                    override fun onAPIResponse(
                        call: Call<AvailableCurrencyResponse>,
                        response: Response<AvailableCurrencyResponse>
                    ) {
                        val responseBody = response.body();
                        if (responseBody?.errorModel != null && responseBody.errorModel?.message != null) {
                            hideProgressLoader()
                            onGetAvailableCurrenciesListener.onGetAvailableCurrenciesError(
                                responseBody.errorModel?.message!!,
                                responseBody.errorModel!!
                            )
                        } else {
                            hideProgressLoader()
                            onGetAvailableCurrenciesListener.onGetAvailableCurrenciesSuccess(
                                responseBody
                            )
                        }
                    }

                    override fun onAPIFailure(
                        call: Call<AvailableCurrencyResponse>,
                        throwable: Throwable
                    ) {
                        hideProgressLoader()
                        onGetAvailableCurrenciesListener.onUnexpectedError(
                            apiType,
                            errorModel.message
                        )
                    }

                    override fun onAPIError(
                        call: Call<AvailableCurrencyResponse>,
                        errorModel: ErrorModel
                    ) {
                        hideProgressLoader()
                        onGetAvailableCurrenciesListener.onGetAvailableCurrenciesError(
                            errorModel.message,
                            errorModel
                        )
                    }

                })
            } else {
                Utils.showInternetErrorPopup(
                    activity,
                    object : Utils.Companion.OnNetworkRetryListener {
                        override fun onRetry() {
                            getAvailableCurrencies(
                                accessKey,
                                onGetAvailableCurrenciesListener
                            )
                        }
                    })
            }
        } else {
            Utils.showToast(activity, activity.getString(R.string.fetched_from_mock_api_call))
            hideProgressLoader()
            val responseBody: AvailableCurrencyResponse = Gson().fromJson(
                Utils.getResponseFromJsonFile(
                    activity,
                    R.raw.available_currencies
                ), AvailableCurrencyResponse::class.java
            )
            onGetAvailableCurrenciesListener.onGetAvailableCurrenciesSuccess(responseBody)
        }
    }

    fun getLiveExchangeRates(
        accessKey: String,
        onGetLiveExchangeRateListener: APICallBackListener.OnGetLiveExchangeRateListener
    ) {
        if (!SettingsUtils.makeMockAPICalls()) {
            if (NetworkUtils(activity).isNetworkAvailable()) {
                val call = CurrencyConversionServiceAdapter(activity).getCurrencyConversionAdapter()
                    ?.getLiveExchangeRate(accessKey)
                showProgressLoader(activity.getString(R.string.fetching_latest_exhange_rates))

                call?.enqueue(object : BaseRetrofitCallbackListener<LiveExchangeRateResponse>(activity) {
                    override fun onAPIResponse(
                        call: Call<LiveExchangeRateResponse>,
                        response: Response<LiveExchangeRateResponse>
                    ) {
                        val responseBody = response.body();
                        if (responseBody?.errorModel != null && responseBody.errorModel?.message != null) {
                            hideProgressLoader()
                            onGetLiveExchangeRateListener.onGetLiveExchangeRateError(
                                responseBody.errorModel?.message!!,
                                responseBody.errorModel!!
                            )
                        } else {
                            hideProgressLoader()
                            onGetLiveExchangeRateListener.onGetLiveExchangeRateSuccess(response.body())
                        }
                    }

                    override fun onAPIFailure(
                        call: Call<LiveExchangeRateResponse>,
                        throwable: Throwable
                    ) {
                        hideProgressLoader()
                        onGetLiveExchangeRateListener.onUnexpectedError(apiType, errorModel.message)
                    }

                    override fun onAPIError(
                        call: Call<LiveExchangeRateResponse>,
                        errorModel: ErrorModel
                    ) {
                        hideProgressLoader()
                        onGetLiveExchangeRateListener.onGetLiveExchangeRateError(
                            errorModel.message,
                            errorModel
                        )
                    }

                })
            } else {
                Utils.showInternetErrorPopup(
                    activity,
                    object : Utils.Companion.OnNetworkRetryListener {
                        override fun onRetry() {
                            getLiveExchangeRates(accessKey, onGetLiveExchangeRateListener)
                        }
                    })
            }
        } else {
            Utils.showToast(activity, activity.getString(R.string.fetched_from_mock_api_call))
            hideProgressLoader()
                val responseBody: LiveExchangeRateResponse = Gson().fromJson(
                    Utils.getResponseFromJsonFile(
                        activity,
                        R.raw.latest_currency_rate
                    ), LiveExchangeRateResponse::class.java
                )
                onGetLiveExchangeRateListener.onGetLiveExchangeRateSuccess(responseBody)
            }
    }
}