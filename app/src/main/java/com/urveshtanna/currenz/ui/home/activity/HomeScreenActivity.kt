package com.urveshtanna.currenz.ui.home.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.urveshtanna.currenz.R
import com.urveshtanna.currenz.databinding.ActivityHomeScreenBinding
import com.urveshtanna.currenz.domain.dataModel.AvailableCurrencyResponse
import com.urveshtanna.currenz.domain.dataModel.CurrencyRateDetails
import com.urveshtanna.currenz.domain.dataModel.ErrorModel
import com.urveshtanna.currenz.domain.dataModel.LiveExchangeRateResponse
import com.urveshtanna.currenz.domain.local.sharedPreferences.AppSharedPreferences
import com.urveshtanna.currenz.domain.remote.interfaces.callbacks.APICallBackListener
import com.urveshtanna.currenz.domain.remote.repositories.CurrencyConversionServerCalls
import com.urveshtanna.currenz.ui.Utils
import com.urveshtanna.currenz.ui.home.adapter.ExchangeRateAdapter
import com.urveshtanna.currenz.ui.selection.Constants
import com.urveshtanna.currenz.ui.selection.activity.CurrencySelectionActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeScreenActivity : AppCompatActivity(),
    APICallBackListener.OnGetAvailableCurrenciesListener, APICallBackListener.OnProgressListener,
    APICallBackListener.OnGetLiveExchangeRateListener, ExchangeRateAdapter.OnRefreshClickListener {

    val TAG = HomeScreenActivity::class.java.simpleName
    var binding: ActivityHomeScreenBinding? = null
    var availableCurrencyMap: HashMap<String, String>? = HashMap()
    var lastUpdatedTimeStamp: Long? = null

    val exchangeRate: MutableList<CurrencyRateDetails> = ArrayList()
    var exchangeSymbolRateMap: HashMap<String, Float> = HashMap()

    var adapter = ExchangeRateAdapter(this, exchangeRate, this)
    var selectedFilterMode = 0
    var lastRefreshedOn: Long = 0;
    var refreshHandler: Handler? = null
    var REFRESH_HANDLER_MESSAGE: Int = 1235

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityHomeScreenBinding>(
            this,
            R.layout.activity_home_screen
        )
        setUpUIComponents()
        fetchAvailableCurrencies()
    }

    /**
     * This function is api call to fetch the available list of currencies
     */
    private fun fetchAvailableCurrencies() {
        CurrencyConversionServerCalls(
            this,
            this
        ).getAvailableCurrencies(getString(R.string.access_key), this)
    }

    /**
     * This function is api call to fetch live exchange rates
     */
    private fun fetchLiveExchangeRate() {
        CurrencyConversionServerCalls(
            this,
            this
        ).getLiveExchangeRates(getString(R.string.access_key), this)
    }

    /**
     * This UI function is called to initalise the UI components like recyclerview adapter, buttons, edittext
     */
    private fun setUpUIComponents() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    ExchangeRateAdapter.VIEW_TYPE_RATE -> 1
                    ExchangeRateAdapter.VIEW_TYPE_FOOTER -> 3
                    else -> -1
                }
            }
        }
        binding?.recyclerView?.layoutManager = gridLayoutManager
        binding?.recyclerView?.adapter = adapter

        binding?.btnChangeCurrency?.setOnClickListener { onChangeCurrencyClick() }

        binding?.edtAmount?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateCurrency()
            }

        })

        binding?.btnFilter?.setOnClickListener { sortDialog() }
    }


    /**
     * This function is called to show a sorting dialog. To sort the currency list
     */
    private fun sortDialog() {
        val listItems = resources.getStringArray(R.array.sorting_options)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.sort_by))
        builder.setSingleChoiceItems(
            listItems,
            selectedFilterMode
        ) { dialogInterface, i ->
            selectedFilterMode = i
            val list = sortList(selectedFilterMode, exchangeRate)
            exchangeRate.clear()
            exchangeRate.addAll(list)
            updateCurrency()
            dialogInterface.dismiss()

        }
        builder.create().show()
    }

    /**
     * @param sortMode
     * @return sorted list of currency rates
     * This function is used to sort currency exchange rate list based on selection
     */
    private fun sortList(
        sortMode: Int,
        listToSort: MutableList<CurrencyRateDetails>
    ): List<CurrencyRateDetails> {
        return when (sortMode) {
            0 -> {
                listToSort.sortedBy { it.name }
            }
            1 -> {
                listToSort.sortedByDescending { it.name }
            }
            2 -> {
                listToSort.sortedBy { it.rate }
            }
            3 -> {
                listToSort.sortedByDescending { it.rate }
            }
            4 -> {
                listToSort.sortedBy { it.symbol }
            }
            else -> {
                listToSort
            }
        }
    }

    /**
     * This function is called to change base currency
     */
    private fun onChangeCurrencyClick() {
        val bundle = Bundle()
        bundle.putString(
            CurrencySelectionActivity.PARCELABLE_AVAILABLE_CURRENCIES,
            Utils.mapToString(availableCurrencyMap)
        )
        CurrencySelectionActivity.newInstance(this, bundle)
    }

    /**
     * This function is used to start countdown to refresh live rates it is called once the first live rates are fetched
     */
    fun createRefreshTimer() {
        if (refreshHandler != null && refreshHandler?.hasMessages(REFRESH_HANDLER_MESSAGE)!!) {
            refreshHandler?.removeMessages(REFRESH_HANDLER_MESSAGE)
        }
        refreshHandler = Handler()
        refreshHandler?.postDelayed({
            fetchLiveExchangeRate()
        }, Constants.REFRESH_TIME_TO_FETCH_LIVE_RATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Checking is user has selected any currency and update the UI
        if (requestCode == CurrencySelectionActivity.REQUEST_FOR_CURRENCY) {
            if (resultCode == Activity.RESULT_OK) {
                updateCurrency()
            }
        }
    }

    /**
     * This function is called to update the recyclerview adapter and also to calculate the exchange rate & value
     */
    private fun updateCurrency() {
        binding?.btnChangeCurrency?.text = AppSharedPreferences(this).getUserPreferredCurrency()
        if (!binding?.edtAmount?.text.isNullOrEmpty()) {
            Utils.showView(binding?.recyclerView, binding?.btnFilter)
            val userInput: Float = binding?.edtAmount?.text.toString().toFloat()
            val userSymbol: String? = AppSharedPreferences(this).getUserPreferredCurrency()
            val usdUserSymbolRate: Float? =
                exchangeSymbolRateMap["${Constants.DEFAULT_SOURCE}${userSymbol}"]

            adapter.exchangeRateList.forEach {
                it.rate =
                    (exchangeSymbolRateMap["${Constants.DEFAULT_SOURCE}${it.symbol}"]!! / usdUserSymbolRate!!)
                it.exchangeValue = it.rate * userInput
            }
            adapter.notifyDataSetChanged()
        } else {
            Utils.hideView(binding?.recyclerView, binding?.btnFilter)
        }
    }


    override fun onGetAvailableCurrenciesSuccess(availableCurrencyResponse: AvailableCurrencyResponse?) {
        binding?.btnChangeCurrency?.text = AppSharedPreferences(this).getUserPreferredCurrency()
        availableCurrencyMap?.putAll(availableCurrencyResponse?.currencies!!.toList())
        fetchLiveExchangeRate()
    }

    override fun onGetAvailableCurrenciesError(errorMsg: String, errorModel: ErrorModel) {
        onUnexpectedError(-1, errorMsg)
    }

    override fun onGetLiveExchangeRateSuccess(liveExchangeRateResponse: LiveExchangeRateResponse?) {
        lastRefreshedOn = Date().time
        lastUpdatedTimeStamp = liveExchangeRateResponse?.timestamp
        var tempList: MutableList<CurrencyRateDetails> = ArrayList()
        liveExchangeRateResponse?.quotes!!.forEach {
            val currencyRateDetails = CurrencyRateDetails()
            currencyRateDetails.symbol = it.key.substring(3, 6)
            currencyRateDetails.rate = it.value
            currencyRateDetails.name = availableCurrencyMap?.get(currencyRateDetails.symbol!!)
            currencyRateDetails.lastUpdated = lastUpdatedTimeStamp
            tempList.add(currencyRateDetails)
        }
        exchangeSymbolRateMap = liveExchangeRateResponse.quotes!!
        createRefreshTimer()
        exchangeRate.clear()
        exchangeRate.addAll(sortList(selectedFilterMode, tempList))
        updateCurrency()
    }

    override fun onGetLiveExchangeRateError(errorMsg: String, errorModel: ErrorModel) {
        onUnexpectedError(-1, errorMsg)
    }

    override fun onUnexpectedError(apiType: Int, errorMsg: String) {
        Utils.showDialog(
            this,
            getString(R.string.error),
            errorMsg,
            getString(R.string.retry),
            null,
            object : Utils.Companion.OnErrorMessageDialogListener {
                override fun onPositiveActionClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }

                override fun onNegativeActionClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }

            })
    }

    override fun onShowProgressLoader(message: String) {
        Utils.hideView(binding?.content)
        Utils.showView(binding?.progressCircular)
    }

    override fun hideProgressLoader() {
        Utils.hideView(binding?.progressCircular)
        Utils.showView(binding?.content)
    }

    override fun onRefreshNowClick() {
        exchangeRate.clear()
        fetchLiveExchangeRate()
    }

    override fun onStop() {
        if (refreshHandler != null && refreshHandler?.hasMessages(REFRESH_HANDLER_MESSAGE)!!) {
            refreshHandler!!.removeCallbacksAndMessages(REFRESH_HANDLER_MESSAGE)
        }
        super.onStop()
    }
}
