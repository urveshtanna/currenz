package com.urveshtanna.currenz.ui.home.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.urveshtanna.currenz.R
import com.urveshtanna.currenz.databinding.ItemCurrencyRateBinding
import com.urveshtanna.currenz.domain.dataModel.CurrencyRateDetails
import com.urveshtanna.currenz.domain.local.sharedPreferences.AppSharedPreferences

class ExchangeRateAdapter(var context: Context, var exchangeRateList: List<CurrencyRateDetails>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemCurrencyRateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_currency_rate,
            parent,
            false
        )
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return exchangeRateList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.binding.currencyRate = exchangeRateList[holder.adapterPosition]
            holder.binding.root.setOnClickListener({
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle(exchangeRateList.get(holder.adapterPosition).name)
                dialog.setMessage(
                    "\nSymbol: ${exchangeRateList.get(holder.adapterPosition).symbol}\n\n" +
                            "Exchange rate: ${exchangeRateList.get(holder.adapterPosition).rate} ${AppSharedPreferences(
                                context
                            ).getUserPreferredCurrency()}\n\n" +
                            "Exchange value: ${exchangeRateList.get(holder.adapterPosition).exchangeValue}\n\n" +
                            "Last updated: ${exchangeRateList.get(holder.adapterPosition).formattedLastUpdated()}\n"

                )
                dialog.setPositiveButton(context.getString(R.string.dismiss),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            p0?.dismiss()
                        }
                    })
                dialog.create().show()
            })
        }
    }

    inner class Holder(var binding: ItemCurrencyRateBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}