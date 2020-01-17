package com.urveshtanna.currenz.ui.selection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.urveshtanna.currenz.R
import com.urveshtanna.currenz.databinding.ItemCurrencyDetailsBinding
import com.urveshtanna.currenz.domain.dataModel.CurrencyDetails
import java.util.*
import kotlin.collections.ArrayList


class CurrencySelectionAdapter(var context: Context, var currencies: List<CurrencyDetails>, var onCurrencyClickListener: OnCurrencyClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var currenciesFiltered: List<CurrencyDetails> = currencies


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemCurrencyDetailsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_currency_details,
            parent,
            false
        )
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return currenciesFiltered.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.binding.currencyDetails = currenciesFiltered[holder.adapterPosition]
            holder.binding.content.setOnClickListener {
                onCurrencyClickListener.onCurrencyClick(currenciesFiltered[holder.adapterPosition])
            }
        }
    }

    inner class Holder(var binding: ItemCurrencyDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnCurrencyClickListener {
        fun onCurrencyClick(currency: CurrencyDetails)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    currenciesFiltered = currencies
                } else {
                    val filteredList: MutableList<CurrencyDetails> = ArrayList()
                    for (row in currencies) {
                        if (row.name?.toLowerCase(Locale.getDefault())?.contains(charString.toLowerCase(Locale.getDefault()))!! || row.symbol?.toLowerCase(
                                Locale.getDefault()
                            )?.contains(charString.toLowerCase(Locale.getDefault()))!!
                        ) {
                            filteredList.add(row)
                        }
                    }
                    this@CurrencySelectionAdapter.currenciesFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = currenciesFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults) {
                currenciesFiltered = filterResults.values as List<CurrencyDetails>
                // refresh the list with filtered data
                notifyDataSetChanged()
            }
        }
    }
}