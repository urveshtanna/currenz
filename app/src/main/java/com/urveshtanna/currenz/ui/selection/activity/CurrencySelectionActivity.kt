package com.urveshtanna.currenz.ui.selection.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.urveshtanna.currenz.R
import com.urveshtanna.currenz.databinding.ActivityCurrencySelectionBinding
import com.urveshtanna.currenz.domain.dataModel.CurrencyDetails
import com.urveshtanna.currenz.domain.local.sharedPreferences.AppSharedPreferences
import com.urveshtanna.currenz.ui.Utils
import com.urveshtanna.currenz.ui.selection.adapter.CurrencySelectionAdapter

class CurrencySelectionActivity : AppCompatActivity(),
    CurrencySelectionAdapter.OnCurrencyClickListener {

    private var availableCurrencyMap: HashMap<String, String>? = HashMap()
    var binding: ActivityCurrencySelectionBinding? = null
    var adapter: CurrencySelectionAdapter? = null

    companion object {

        var REQUEST_FOR_CURRENCY = 143
        var PARCELABLE_AVAILABLE_CURRENCIES = "available_currencies"

        fun newInstance(activity: Activity, bundle: Bundle) {
            val intent = Intent(activity, CurrencySelectionActivity::class.java)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, REQUEST_FOR_CURRENCY)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_currency_selection_activity, menu)
        val mSearch: MenuItem = menu.findItem(R.id.appSearchBar)
        val mSearchView: SearchView = mSearch.actionView as SearchView
        mSearchView.maxWidth = Integer.MAX_VALUE
        mSearchView.queryHint = getString(R.string.search_by_name_symbol)
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_currency_selection)
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding?.collapsingToolbar?.isTitleEnabled = false
        supportActionBar?.title = getString(R.string.select_your_currency)

        availableCurrencyMap =
            Utils.stringToMap(intent.getStringExtra(PARCELABLE_AVAILABLE_CURRENCIES))
        setUpSelectionAdapter()
    }

    private fun setUpSelectionAdapter() {
        val currencyList: MutableList<CurrencyDetails> = ArrayList()
        availableCurrencyMap?.forEach {
            val currencyDetails = CurrencyDetails()
            currencyDetails.name = it.value
            currencyDetails.symbol = it.key
            currencyList.add(currencyDetails)
        }
        val list = currencyList.sortedBy { it.name }
        adapter = CurrencySelectionAdapter(this, list, this)
        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        binding?.recyclerView?.adapter = adapter
    }

    override fun onCurrencyClick(currency: CurrencyDetails) {
        AppSharedPreferences(this).setUserPreferredCurrency(currency.symbol)
        setResult(Activity.RESULT_OK)
        finish()
    }
}
