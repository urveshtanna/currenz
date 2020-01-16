package com.urveshtanna.currenz.ui.home.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
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
import com.urveshtanna.currenz.ui.selection.activity.CurrencySelectionActivity

class HomeScreenActivity : AppCompatActivity(),
    APICallBackListener.OnGetAvailableCurrenciesListener, APICallBackListener.OnProgressListener,
    APICallBackListener.OnGetLiveExchangeRateListener {

    val TAG = HomeScreenActivity::class.java.simpleName
    var binding: ActivityHomeScreenBinding? = null
    var availableCurrencyMap: HashMap<String, String>? = HashMap()
    var lastUpdatedTimeStamp: Long? = null

    val exchangeRate: MutableList<CurrencyRateDetails> = ArrayList<CurrencyRateDetails>()
    var exchangeSymbolRateMap: HashMap<String, Double> = HashMap()

    var adapter = ExchangeRateAdapter(this, exchangeRate)
    var selectedFilterMode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityHomeScreenBinding>(
            this,
            R.layout.activity_home_screen
        )
        binding?.recyclerView?.layoutManager = GridLayoutManager(this, 3)
        binding?.recyclerView?.adapter = adapter
        binding?.btnChangeCurrency?.setOnClickListener({
            onChangeCurrencyClick();
        })

        binding?.edtAmount?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateCurrency()
            }

        })

        binding?.btnFilter?.setOnClickListener({
            filterDialog()
        })

        CurrencyConversionServerCalls(
            this,
            this
        ).getAvailableCurrencies(getString(R.string.access_key), this)
    }

    private fun filterDialog() {
        val listItems =
            arrayOf(
                "Alphabetically",
                "Alphabetically Desc",
                "Highest Rate",
                "Lowest Rate",
                "Symbol"
            )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sort by")
        builder.setSingleChoiceItems(
            listItems,
            selectedFilterMode,
            { dialogInterface, i ->
                selectedFilterMode = i
                val list = filterList(selectedFilterMode)
                exchangeRate.clear()
                exchangeRate.addAll(list)
                updateCurrency()
                dialogInterface.dismiss()

            });

        builder.create().show()
    }

    private fun filterList(filterMode: Int): List<CurrencyRateDetails> {
        if (filterMode == 0) {
            val list = adapter.exchangeRateList.sortedBy { it.name }
            return list
        } else if (filterMode == 1) {
            val list = adapter.exchangeRateList.sortedByDescending { it.name }
            return list
        } else if (filterMode == 2) {
            val list = adapter.exchangeRateList.sortedBy { it.rate }
            return list
        } else if (filterMode == 3) {
            val list = adapter.exchangeRateList.sortedByDescending { it.rate }
            return list
        } else if (filterMode == 4) {
            val list = adapter.exchangeRateList.sortedBy { it.symbol }
            return list
        } else {
            return adapter.exchangeRateList
        }
    }

    private fun onChangeCurrencyClick() {
        val bundle = Bundle()
        bundle.putString(
            CurrencySelectionActivity.PARCELABLE_AVAILABLE_CURRENCIES,
            Utils.mapToString(availableCurrencyMap)
        )
        CurrencySelectionActivity.newInstance(this, bundle)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CurrencySelectionActivity.REQUEST_FOR_CURRENCY) {
            if (resultCode == Activity.RESULT_OK) {
                updateCurrency();
            }
        }
    }

    private fun updateCurrency() {
        binding?.btnChangeCurrency?.setText(AppSharedPreferences(this).getUserPreferredCurrency())
        if (!binding?.edtAmount?.text.isNullOrEmpty()) {
            Utils.showView(binding?.recyclerView, binding?.btnFilter)
            val userInput: Double = binding?.edtAmount?.text.toString().toDouble()
            val userSymbol: String? = AppSharedPreferences(this).getUserPreferredCurrency()
            val USDUserSymbolRate: Double? = exchangeSymbolRateMap.get("USD" + userSymbol)

            adapter.exchangeRateList.forEach({
                it.rate = (exchangeSymbolRateMap.get("USD" + it.symbol)!! / USDUserSymbolRate!!)
                it.exchangeValue = it.rate * userInput
            })
            adapter.notifyDataSetChanged()
        } else {
            Utils.hideView(binding?.recyclerView, binding?.btnFilter)
        }
    }


    override fun onGetAvailableCurrenciesSuccess(availableCurrencyResponse: AvailableCurrencyResponse?) {
        binding?.btnChangeCurrency?.setText(AppSharedPreferences(this).getUserPreferredCurrency())
        availableCurrencyMap?.putAll(availableCurrencyResponse?.currencies!!.toList())

        CurrencyConversionServerCalls(
            this,
            this
        ).getLiveExchangeRates(getString(R.string.access_key), this)
    }

    override fun onGetAvailableCurrenciesError(errorMsg: String, errorModel: ErrorModel) {
        onUnexpectedError(-1, errorMsg)
    }

    override fun onGetLiveExchangeRateSuccess(liveExchangeRateResponse: LiveExchangeRateResponse?) {
        lastUpdatedTimeStamp = liveExchangeRateResponse?.timestamp
        liveExchangeRateResponse?.quotes!!.forEach {
            val currencyRateDetails = CurrencyRateDetails()
            currencyRateDetails.symbol = it.key.substring(3, 6)
            currencyRateDetails.rate = it.value
            currencyRateDetails.name = availableCurrencyMap?.get(currencyRateDetails.symbol!!)
            currencyRateDetails.lastUpdated = lastUpdatedTimeStamp
            exchangeRate.add(currencyRateDetails)
        }
        val list = filterList(selectedFilterMode)
        exchangeRate.clear()
        exchangeRate.addAll(list)
        exchangeSymbolRateMap = liveExchangeRateResponse.quotes!!
        updateCurrency()
    }

    override fun onGetLiveExchangeRateError(errorMsg: String, errorModel: ErrorModel) {
        onUnexpectedError(-1, errorMsg)
    }

    override fun onUnexpectedError(apiType: Int, errorMsg: String) {
        Utils.showErrorDialog(
            this,
            getString(R.string.error),
            errorMsg,
            getString(R.string.retry),
            getString(R.string.retry),
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
}
