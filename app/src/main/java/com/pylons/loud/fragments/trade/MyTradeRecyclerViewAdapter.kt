package com.pylons.loud.fragments.trade

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.pylons.loud.R


import com.pylons.loud.fragments.trade.TradeFragment.OnListFragmentInteractionListener
import com.pylons.loud.models.trade.*

import kotlinx.android.synthetic.main.fragment_trade_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [Trade] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyTradeRecyclerViewAdapter(
    private val mValues: List<Trade>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyTradeRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Trade
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            if (item.isMyTrade) {
                mListener?.onCancel(item)
            } else {
                mListener?.onTrade(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_trade_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        when (item) {
            is LoudTrade -> holder.mContentView.text =
                ("${item.output.amount} ${item.output.coin}\nWant: ${item.input.amount} ${item.input.coin}")
            is SellItemTrade -> holder.mContentView.text =
                ("${item.output.name} Lv${item.output.level}\nWant: ${item.input.amount} ${item.input.coin}")
            is BuyItemTrade -> holder.mContentView.text =
                ("${item.output.amount} ${item.output.coin}\nWant: ${item.input.name} Lv${item.input.level}")
        }

        if (item.isMyTrade) {
            holder.mAction.text = "Cancel"
        } else {
            holder.mAction.text = "Trade"
        }

        with(holder.mAction) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.content
        val mAction: Button = mView.action

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
