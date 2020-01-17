package com.urveshtanna.currenz.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.urveshtanna.currenz.R
import com.urveshtanna.currenz.databinding.ItemCurrencyRateBinding
import com.urveshtanna.currenz.databinding.ItemCurrencyRateFooterBinding
import com.urveshtanna.currenz.domain.dataModel.CurrencyRateDetails
import com.urveshtanna.currenz.domain.local.sharedPreferences.AppSharedPreferences

class ExchangeRateAdapter(var context: Context, var exchangeRateList: List<CurrencyRateDetails>, var onRefreshClickListener: OnRefreshClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        var VIEW_TYPE_RATE = 0
        var VIEW_TYPE_FOOTER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_RATE) {
            val binding: ItemCurrencyRateBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_currency_rate,
                parent,
                false
            )
            return Holder(binding)
        } else {
            val binding: ItemCurrencyRateFooterBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_currency_rate_footer,
                parent,
                false
            )

            return FooterHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < exchangeRateList.size) {
            return VIEW_TYPE_RATE
        } else {
            return VIEW_TYPE_FOOTER
        }
    }

    override fun getItemCount(): Int {
        return exchangeRateList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.binding.currencyRate = exchangeRateList[holder.adapterPosition]
            holder.binding.root.setOnClickListener {
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle(exchangeRateList[holder.adapterPosition].name)
                dialog.setMessage(context.getString(R.string.currency_details_message,
                    exchangeRateList[holder.adapterPosition].symbol,
                    exchangeRateList[holder.adapterPosition].rate.toBigDecimal().toPlainString(),
                    AppSharedPreferences(context).getUserPreferredCurrency(),
                    exchangeRateList[holder.adapterPosition].formattedExchangeValue(),
                    exchangeRateList[holder.adapterPosition].formattedLastUpdated()))
                dialog.setPositiveButton(context.getString(R.string.dismiss)
                ) { p0, p1 -> p0?.dismiss() }
                dialog.create().show()
            }
        } else if(holder is FooterHolder){
            holder.binding.tvRefreshedOn.text = context.getString(R.string.as_of_variable, exchangeRateList[0].formattedLastUpdated())
            holder.binding.btnRefreshNow.setOnClickListener {
                onRefreshClickListener.onRefreshNowClick()
            }
        }
    }

    inner class Holder(var binding: ItemCurrencyRateBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class FooterHolder(var binding: ItemCurrencyRateFooterBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnRefreshClickListener {
        fun onRefreshNowClick()
    }
}